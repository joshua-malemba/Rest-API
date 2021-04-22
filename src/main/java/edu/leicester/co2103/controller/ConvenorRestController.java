package edu.leicester.co2103.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import edu.leicester.co2103.domain.Convenor;
import edu.leicester.co2103.domain.Module;

import edu.leicester.co2103.domain.ErrorInfo;
import edu.leicester.co2103.repo.ConvenorRepository;
import java.lang.Math;
import java.util.*;
@RestController
public class ConvenorRestController {
	
	@Autowired 
	private ConvenorRepository crepo;
	@GetMapping("/convenors")
	public ResponseEntity<List> listConvenors() {
		/// list all convenors in repository 
		
		List<Convenor> convenors = crepo.findAll();
		if (convenors.isEmpty()) {
			return new ResponseEntity(new ErrorInfo("No Convenors Found!"), HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List>(convenors, HttpStatus.OK);
		
}
	
	@GetMapping("/convenors/{id}")
	public ResponseEntity<?> getConvenor(@PathVariable("id") Long id){		
		Convenor convenor = crepo.findById(id).orElse(null);
		
		if(convenor == null) {
			return new ResponseEntity(new ErrorInfo ("Convenor with id " + id + " not found"), HttpStatus.NOT_FOUND);
			}
		return new ResponseEntity<Convenor>(convenor, HttpStatus.OK);
		}
	
	@GetMapping("/convenors/{id}/modules")
	public ResponseEntity<?> listModulesByConvenor(@PathVariable("id") Long id){
		Convenor convenor = crepo.findById(id).orElse(null);
		
		if(convenor == null) {
			return new ResponseEntity(new ErrorInfo ("Convenor with id " + id + " not found"), HttpStatus.NOT_FOUND);
			}
		
		List<Module> modules = new ArrayList<>(); 
		modules = convenor.getModules();
		return new ResponseEntity<List>(modules, HttpStatus.OK);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes"})
	@PostMapping("/convenors/{id}")
	public ResponseEntity<?> createConvenor(@RequestBody Convenor convenors, UriComponentsBuilder ucBuilder, @PathVariable("id") Long id) {
		if (crepo.existsById(id)) {
			return new ResponseEntity(new ErrorInfo("A convenor by the name of " + convenors.getName() + " already exists."), HttpStatus.CONFLICT);
			} 
		// THis is how we redirect to a given URL with API's 
		convenors = crepo.save(convenors);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/convenors/{id}").buildAndExpand(convenors.getId()).toUri());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PutMapping("/convenors/{id}")
	public ResponseEntity<?> updateConvenor(@PathVariable("id") Long id, @RequestBody Convenor convenors) {
		Convenor currentConvenor = crepo.findAllById(id);
		
		if (currentConvenor == null) {
			return new ResponseEntity(new ErrorInfo("Convenor with the id not found"), HttpStatus.NOT_FOUND);
		}
		/// update convenor by given id with new details 
		currentConvenor.setName(convenors.getName());
		currentConvenor.setId(convenors.getId());
		currentConvenor.setModules(convenors.getModules());
		currentConvenor = crepo.save(currentConvenor);
		return new ResponseEntity<Convenor>(currentConvenor, HttpStatus.OK);
		
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	@DeleteMapping("/convenors/{id}")
	public ResponseEntity<?> deleteConvenor(@PathVariable("id") Long id) {
		
		Convenor currentConvenor = crepo.findAllById(id);
		if (currentConvenor == null) {
			return new ResponseEntity(new ErrorInfo("Convenor with the id not found"), HttpStatus.NOT_FOUND);
		}
		
		// Deleting a convenor should delete all the modules they teach 
		currentConvenor.setModules(null);
		crepo.deleteById(id);		
		
		List<Convenor> convenors = crepo.findAll();
		return new ResponseEntity<List>(convenors, HttpStatus.OK);
	}
	
}
