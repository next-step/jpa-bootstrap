package fixture;

import domain.Employee;

import java.util.Arrays;
import java.util.List;

public class EmployeeFixtureFactory {

    public static Employee getFixture() {
        return new Employee("홍길동", 1L);
    }

    public static List<Employee> getFixtures() {
        return Arrays.asList(
                new Employee("라이언", 1L),
                new Employee("어피치", 1L),
                new Employee("춘식이", 1L)
        );
    }

}
