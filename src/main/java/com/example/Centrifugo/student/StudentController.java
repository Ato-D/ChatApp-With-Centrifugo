package com.example.Centrifugo.student;


import com.example.Centrifugo.dto.ResponseDTO;
import com.example.Centrifugo.dto.StudentDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/students")
@AllArgsConstructor
public class StudentController {


    private final StudentService studentService;


    @GetMapping("/findAll")
    public ResponseEntity<ResponseDTO> findAll(@RequestParam Map<String, String> params) {
        return studentService.findAllStudents(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO> findById(@PathVariable(name = "id") UUID id) {
        var res = studentService.findById(id);
        return res;
    }


    @PostMapping("/createStudent")
    public ResponseEntity<ResponseDTO> save(@RequestBody StudentDto studentDto) {
        return studentService.saveStudent(studentDto);
    }

    @PutMapping("updateStudent/{id}")
    public ResponseEntity<ResponseDTO> update(@PathVariable(name = "id") UUID id,
                                              @RequestBody StudentDto studentDto) {
        studentDto.setId(id);
        return studentService.updateStudent(id, studentDto);
    }

    @DeleteMapping("/deleteStudent/{id}")
    public ResponseEntity<ResponseDTO> delete(@PathVariable(name = "id") UUID id) {
        return studentService.deleteStudent(id);
    }
}
