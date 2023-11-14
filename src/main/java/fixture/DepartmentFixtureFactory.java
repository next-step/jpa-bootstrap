package fixture;

import domain.Department;

public class DepartmentFixtureFactory {

    public static Department getFixture() {
        return new Department("ATeam");
    }
}
