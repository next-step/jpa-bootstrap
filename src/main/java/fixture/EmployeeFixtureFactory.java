package fixture;

import domain.Employee;

public class EmployeeFixtureFactory {

    public static Employee getFixture() {
        return new Employee("홍길동");
    }
}
