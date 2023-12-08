package domain.helper;

import domain.Department;
import domain.Employee;
import domain.Order;
import domain.OrderItem;

public class DepartmentSqlHelper {
    public static String 부서_테이블_생성_쿼리() {
        return "create table department (\n" +
            "        id bigint generated by default as identity,\n" +
            "        name varchar(255),\n" +
            "        primary key (id)\n" +
            "    )";
    }

    public static String 직원_테이블_생성_쿼리() {
        return "create table employee (\n" +
            "        id bigint generated by default as identity,\n" +
            "        name varchar(255),\n" +
            "        department_id bigint,\n" +
            "        primary key (id)\n" +
            "    )";
    }

    public static String 부서_저장하는_쿼리(Department department) {
        return String.format("INSERT INTO department (id, name) VALUES(%d, '%s')", department.getId(), department.getName());
    }

    public static String 직원_저장하는_쿼리(Employee item, Department department) {
        return String.format("INSERT INTO employee (id, name, department_id) VALUES(%d, '%s', %d)", item.getId(), item.getName(), department.getId());
    }
}
