param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$OutputPath = "route_test_results.json"
)

$ErrorActionPreference = "Stop"

function New-TestResult {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [object]$RequestBody,
        [hashtable]$Headers,
        [int]$StatusCode,
        [object]$ResponseBody,
        [string]$ErrorMessage
    )

    [pscustomobject]@{
        name = $Name
        method = $Method
        url = $Url
        request = [pscustomobject]@{
            headers = $Headers
            body = $RequestBody
        }
        response = [pscustomobject]@{
            status = $StatusCode
            body = $ResponseBody
            error = $ErrorMessage
        }
        timestamp = (Get-Date).ToString("o")
    }
}

function Invoke-Api {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Url,
        [object]$Body = $null,
        [hashtable]$Headers = $null
    )

    $jsonBody = $null
    if ($null -ne $Body) {
        $jsonBody = ($Body | ConvertTo-Json -Depth 10)
    }

    try {
        $resp = Invoke-WebRequest -Method $Method -Uri $Url -Headers $Headers -Body $jsonBody -ContentType "application/json" -UseBasicParsing
        $respBody = $null
        if ($resp.Content) {
            try { $respBody = $resp.Content | ConvertFrom-Json } catch { $respBody = $resp.Content }
        }

        return New-TestResult -Name $Name -Method $Method -Url $Url -RequestBody $Body -Headers $Headers -StatusCode $resp.StatusCode -ResponseBody $respBody -ErrorMessage $null
    } catch {
        $status = $null
        $errorBody = $null
        $errorMessage = $_.Exception.Message

        if ($_.Exception.Response) {
            $status = [int]$_.Exception.Response.StatusCode
            try {
                $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
                $raw = $reader.ReadToEnd()
                try { $errorBody = $raw | ConvertFrom-Json } catch { $errorBody = $raw }
            } catch {
                $errorBody = $null
            }
        }

        return New-TestResult -Name $Name -Method $Method -Url $Url -RequestBody $Body -Headers $Headers -StatusCode $status -ResponseBody $errorBody -ErrorMessage $errorMessage
    }
}

$results = @()

# Generate unique user for this run
$guid = [guid]::NewGuid().ToString("N")
$email = "user_$guid@test.com"
$password = "Passw0rd!123"
$name = "Test User $guid"

# Auth: register
$registerBody = @{ name = $name; email = $email; password = $password }
$registerResult = Invoke-Api -Name "auth_register" -Method "POST" -Url "$BaseUrl/auth/register" -Body $registerBody
$results += $registerResult

# Auth: login
$loginBody = @{ email = $email; password = $password }
$loginResult = Invoke-Api -Name "auth_login" -Method "POST" -Url "$BaseUrl/auth/login" -Body $loginBody
$results += $loginResult

$accessToken = $null
$refreshToken = $null
$userId = $null
if ($loginResult.response.body) {
    $accessToken = $loginResult.response.body.accessToken
    $refreshToken = $loginResult.response.body.refreshToken
}

# Try to capture user id from register/login response payloads if available
if ($registerResult.response.body) {
    if ($registerResult.response.body.id) { $userId = $registerResult.response.body.id }
    elseif ($registerResult.response.body.userId) { $userId = $registerResult.response.body.userId }
    elseif ($registerResult.response.body.user) {
        if ($registerResult.response.body.user.id) { $userId = $registerResult.response.body.user.id }
    }
}
if (-not $userId -and $loginResult.response.body) {
    if ($loginResult.response.body.id) { $userId = $loginResult.response.body.id }
    elseif ($loginResult.response.body.userId) { $userId = $loginResult.response.body.userId }
    elseif ($loginResult.response.body.user) {
        if ($loginResult.response.body.user.id) { $userId = $loginResult.response.body.user.id }
    }
}

$authHeaders = @{}
if ($accessToken) {
    $authHeaders["Authorization"] = "Bearer $accessToken"
}

# Auth: refresh
if ($refreshToken) {
    $refreshBody = @{ refreshToken = $refreshToken }
    $results += Invoke-Api -Name "auth_refresh" -Method "POST" -Url "$BaseUrl/auth/refresh" -Body $refreshBody
} else {
    $results += New-TestResult -Name "auth_refresh" -Method "POST" -Url "$BaseUrl/auth/refresh" -RequestBody @{ refreshToken = $null } -Headers $null -StatusCode $null -ResponseBody $null -ErrorMessage "Login failed; no refresh token available"
}

# Users
$results += Invoke-Api -Name "users_list" -Method "GET" -Url "$BaseUrl/users" -Headers $authHeaders

if ($userId) {
    $results += Invoke-Api -Name "users_get_by_id" -Method "GET" -Url "$BaseUrl/users/$userId" -Headers $authHeaders
    $results += Invoke-Api -Name "users_deactivate" -Method "PATCH" -Url "$BaseUrl/users/$userId/deactivate" -Headers $authHeaders
} else {
    $results += New-TestResult -Name "users_get_by_id" -Method "GET" -Url "$BaseUrl/users/{id}" -RequestBody $null -Headers $authHeaders -StatusCode $null -ResponseBody $null -ErrorMessage "User id not available from auth responses"
    $results += New-TestResult -Name "users_deactivate" -Method "PATCH" -Url "$BaseUrl/users/{id}/deactivate" -RequestBody $null -Headers $authHeaders -StatusCode $null -ResponseBody $null -ErrorMessage "User id not available from auth responses"
}

# Accounts
$accountCreateBody = @{ name = "Main Account" }
$accountCreate = Invoke-Api -Name "accounts_create" -Method "POST" -Url "$BaseUrl/accounts" -Body $accountCreateBody -Headers $authHeaders
$results += $accountCreate

$accountId = $null
if ($accountCreate.response.body) {
    $accountId = $accountCreate.response.body.id
}

$results += Invoke-Api -Name "accounts_list" -Method "GET" -Url "$BaseUrl/accounts" -Headers $authHeaders

if ($accountId) {
    $results += Invoke-Api -Name "accounts_get_by_id" -Method "GET" -Url "$BaseUrl/accounts/$accountId" -Headers $authHeaders
} else {
    $results += New-TestResult -Name "accounts_get_by_id" -Method "GET" -Url "$BaseUrl/accounts/{id}" -RequestBody $null -Headers $authHeaders -StatusCode $null -ResponseBody $null -ErrorMessage "Account creation failed; no id available"
}

# Transactions
if ($accountId) {
    $creditBody = @{ type = "CREDIT"; amount = 100.00 }
    $results += Invoke-Api -Name "transactions_credit" -Method "POST" -Url "$BaseUrl/accounts/$accountId/transactions" -Body $creditBody -Headers $authHeaders

    $debitBody = @{ type = "DEBIT"; amount = 40.00 }
    $results += Invoke-Api -Name "transactions_debit" -Method "POST" -Url "$BaseUrl/accounts/$accountId/transactions" -Body $debitBody -Headers $authHeaders

    # include pagination params
    $results += Invoke-Api -Name "transactions_list" -Method "GET" -Url "$BaseUrl/accounts/$accountId/transactions?page=0&size=20" -Headers $authHeaders
} else {
    $results += New-TestResult -Name "transactions_credit" -Method "POST" -Url "$BaseUrl/accounts/{id}/transactions" -RequestBody @{ type = "CREDIT"; amount = 100.00 } -Headers $authHeaders -StatusCode $null -ResponseBody $null -ErrorMessage "Account creation failed; no id available"
    $results += New-TestResult -Name "transactions_debit" -Method "POST" -Url "$BaseUrl/accounts/{id}/transactions" -RequestBody @{ type = "DEBIT"; amount = 40.00 } -Headers $authHeaders -StatusCode $null -ResponseBody $null -ErrorMessage "Account creation failed; no id available"
    $results += New-TestResult -Name "transactions_list" -Method "GET" -Url "$BaseUrl/accounts/{id}/transactions?page=0&size=20" -RequestBody $null -Headers $authHeaders -StatusCode $null -ResponseBody $null -ErrorMessage "Account creation failed; no id available"
}

# Accounts: delete
if ($accountId) {
    $results += Invoke-Api -Name "accounts_delete" -Method "DELETE" -Url "$BaseUrl/accounts/$accountId" -Headers $authHeaders
} else {
    $results += New-TestResult -Name "accounts_delete" -Method "DELETE" -Url "$BaseUrl/accounts/{id}" -RequestBody $null -Headers $authHeaders -StatusCode $null -ResponseBody $null -ErrorMessage "Account creation failed; no id available"
}

# Save results
$results | ConvertTo-Json -Depth 10 | Set-Content -Encoding UTF8 $OutputPath
Write-Host "Saved results to $OutputPath"
