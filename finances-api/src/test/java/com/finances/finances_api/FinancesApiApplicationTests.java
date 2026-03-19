package com.finances.finances_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"JWT_SECRET=uVdLxq8P1mFQKp1xQ9l5t5H8y8j5kM9c9r9zF3z1lW8=",
		"jwt.secret=uVdLxq8P1mFQKp1xQ9l5t5H8y8j5kM9c9r9zF3z1lW8="
})
class FinancesApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
