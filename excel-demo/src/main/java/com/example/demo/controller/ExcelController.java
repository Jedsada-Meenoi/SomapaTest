package com.example.demo.controller;

import com.example.demo.model.DataList; //Data Model
import com.example.demo.service.ExcelService; //Read Excel
import org.springframework.http.*; 
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.*;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/excel")
public class ExcelController {

	
	 @Autowired
	    private ExcelService excelService;

	    @PostMapping("/upload")
	    public ResponseEntity<?> uploadFile(
	            @RequestParam("file") MultipartFile file) {
	    	
	    	//Debugs
	    	System.out.println("File received: " + file.getOriginalFilename()); 
	        System.out.println("File size: " + file.getSize());
	        
	        String filename = file.getOriginalFilename();
	        if (filename == null || !filename.endsWith(".xlsx")) {
	            return ResponseEntity.badRequest().body("File must be .xlsx"); //Http 400
	        }

	        // Check file size
	        if (file.getSize() > 1 * 1024 * 1024) {
	            return ResponseEntity.badRequest().body("File size must be less than 1 MB");
	        }

	        try {
	        	
	            List<DataList> passengers = excelService.parseAndValidate(file);
	            return ResponseEntity.ok(passengers); 
	        } catch (Exception e) {
	            return ResponseEntity.badRequest().body(e.getMessage());
	        }
	        
	        
	    }
}
