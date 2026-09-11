package com.kubernets;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Employee create(Employee employee) {
        return repository.save(employee);
    }

    public List<Employee> getAll() {
        return repository.findAll();
    }

    public Employee getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Employee not found: " + id));
    }

    public Employee update(Long id, Employee employee) {

        Employee existing = getById(id);

        existing.setName(employee.getName());
        existing.setEmail(employee.getEmail());
        existing.setDepartment(employee.getDepartment());

        return repository.save(existing);
    }

    public void delete(Long id) {
        Employee existing = getById(id);
        repository.delete(existing);
    }
}