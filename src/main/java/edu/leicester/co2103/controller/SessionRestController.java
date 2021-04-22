package edu.leicester.co2103.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.leicester.co2103.domain.Convenor;
import edu.leicester.co2103.domain.ErrorInfo;
import edu.leicester.co2103.domain.Module;

import edu.leicester.co2103.domain.Session;
import edu.leicester.co2103.repo.ConvenorRepository;
import edu.leicester.co2103.repo.ModuleRepository;
import edu.leicester.co2103.repo.SessionRepository;
import java.util.*;

@RestController
public class SessionRestController {
	
	@Autowired
	private SessionRepository srepo;
	
	@Autowired
	private ConvenorRepository crepo;
	
	@Autowired
	private ModuleRepository mrepo;
	
	
	private List<Module> modules = new ArrayList<>();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/sessions/{convenor}/{module}")
	public ResponseEntity<?> listSessionsByConvenor(@PathVariable("convenor") Long convenorId, @PathVariable("module") String moduleCode){
		//// given module code should help identify given module 
		List<Session>sessions = new ArrayList<>();
		Module modulee = mrepo.findById(moduleCode).orElse(null);
		Convenor c = crepo.findById(convenorId).orElse(null);
		
		if(c == null) {
			return new ResponseEntity(new ErrorInfo ("Convenor with the given id not found!"), HttpStatus.NOT_FOUND);
			}
		
			List<Module> moduless = c.getModules();
			for(Module m : moduless) {
				sessions.addAll(m.getSessions());
			}
			
			
				Module mod = mrepo.findById(moduleCode).orElse(null);
				if(mod == null) {
					return new ResponseEntity(new ErrorInfo ("Module with code " + moduleCode + " not found"), HttpStatus.NOT_FOUND);
					}
				sessions = mod.getSessions();
				
				
				return new ResponseEntity<List>(sessions, HttpStatus.OK);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@DeleteMapping("/sessions")
	public ResponseEntity<?> deleteSessions(){
		if(srepo.findAll() == null) {
			return new ResponseEntity(new ErrorInfo("There are no scheduled sessions!"), HttpStatus.NOT_FOUND);
		}
		
		srepo.deleteAll();
		return new ResponseEntity(HttpStatus.OK);
	}
}
