package com.example.employee_manager.controller;


import com.example.employee_manager.model.Employee;
import com.example.employee_manager.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String viewHomePage(Model model, @RequestParam(required = false) String keyword) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("listEmployees", employeeService.searchEmployees(keyword));
        model.addAttribute("keyword", keyword);
        return "employees";
    }

    @PostMapping
    public String saveEmployee(@Valid @ModelAttribute("employee") Employee employee,
                               BindingResult result, Model model) {
        Optional<Employee> existingEmployee = employeeService.getEmployeeByEmail(employee.getEmail());
        if (existingEmployee.isPresent() && (employee.getId() == null || !employee.getId().equals(existingEmployee.get().getId()))) {
            result.rejectValue("email", "error.employee", "This email is already registered");
        }

        if (result.hasErrors()) {
            model.addAttribute("listEmployees", employeeService.getAllEmployees());
            return "employees";
        }
        employeeService.saveEmployee(employee);
        return "redirect:/employees";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee Id:" + id));
        model.addAttribute("employee", employee);
        return "edit-employee";
    }

    @PostMapping("/{id}/update")
    public String updateEmployee(@PathVariable("id") Long id, @Valid @ModelAttribute("employee") Employee employee,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            employee.setId(id);
            return "edit-employee";
        }
        employee.setId(id);
        employeeService.saveEmployee(employee);
        return "redirect:/employees";
    }

    @PostMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/employees";
    }
}
