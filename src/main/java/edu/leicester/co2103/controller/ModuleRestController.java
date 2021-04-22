package edu.leicester.co2103.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import edu.leicester.co2103.repo.ModuleRepository;
import edu.leicester.co2103.repo.SessionRepository;
import edu.leicester.co2103.repo.ConvenorRepository;

import edu.leicester.co2103.domain.Convenor;
import edu.leicester.co2103.domain.ErrorInfo;
import edu.leicester.co2103.domain.Module;
import edu.leicester.co2103.domain.Session;
import java.util.*;

@RestController
public class ModuleRestController {
	
	@Autowired 
	private ModuleRepository mrepo;
	
	@Autowired 
	private SessionRepository srepo;
	
	@Autowired 
	private ConvenorRepository crepo;
	
	@GetMapping("/modules/{code}")
	public ResponseEntity<?> getModule(@PathVariable("code") String code){
		Module module = mrepo.findById(code).orElse(null);
		
		if(module == null) {
			return new ResponseEntity(new ErrorInfo ("Module with code " + code + " not found"), HttpStatus.NOT_FOUND);
			}
		
		return new ResponseEntity<Module>(module, HttpStatus.OK);
	}
	
	@GetMapping("/modules/{code}/sessions/{id}")
	public ResponseEntity<?> getSessionInModule(@PathVariable("id") Long id, @PathVariable("code") String code){
		
		Module module = mrepo.findById(code).orElse(null);
		
		if(module == null) {
			return new ResponseEntity(new ErrorInfo ("Module with code " + code + " not found"), HttpStatus.NOT_FOUND);
			}
				List <Session> sessions = module.getSessions();
				Session sessionn = srepo.findById(id).orElse(null);
				
				if (sessionn == null) {
					return new ResponseEntity(new ErrorInfo ("Session with id " + id + " not found"), HttpStatus.NOT_FOUND);
 
				}
				    return new ResponseEntity<Session>(sessionn, HttpStatus.OK);
						
	}
	
	@GetMapping("/modules/{code}/sessions")
	public ResponseEntity<?> listSessionsInModule(@PathVariable("code") String code){
		List<Session>sessions = new ArrayList<>();
		Module module = mrepo.findById(code).orElse(null);

		if(module == null) {
			return new ResponseEntity(new ErrorInfo ("Module with code " + code + " not found"), HttpStatus.NOT_FOUND);
			}
				sessions = module.getSessions();
				return new ResponseEntity<List>(sessions, HttpStatus.OK);
	}
	
	@RequestMapping("/modules")
	public ResponseEntity<?> listModules(){
		
		List<Module> modules = mrepo.findAll();

		if(mrepo.findAll() == null) {
			return new ResponseEntity(new ErrorInfo ("No Modules Found "), HttpStatus.NOT_FOUND);
			}
		
	    return new ResponseEntity<List>(modules, HttpStatus.OK);

		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/modules/{code}")
	public ResponseEntity<?> createModule(@RequestBody Module modules, @PathVariable("code") String code, UriComponentsBuilder ucBuilder){
		if (mrepo.existsById(code)) {
			return new ResponseEntity(new ErrorInfo("A module by the name " + modules.getTitle() + " already exists."), HttpStatus.CONFLICT);
		}
		
		modules = mrepo.save(modules);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/modules/{code}").buildAndExpand(modules.getCode()).toUri());
		return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PatchMapping("/modules/{code}")
	public ResponseEntity<?> updateModule(@PathVariable("code") String code, @RequestBody Module modules){
		
		Module currentModule = new Module();
		List<Session> sessions = modules.getSessions();
		
		Module module = mrepo.findById(code).orElse(null);
		
		if(module == null) {
			return new ResponseEntity(new ErrorInfo ("Module with code " + code + " not found"), HttpStatus.NOT_FOUND);
			}
				currentModule = module;
				currentModule.setCode(modules.getCode());
				currentModule.setLevel(modules.getLevel());
				currentModule.setSessions(modules.getSessions());
				currentModule.setTitle(modules.getTitle());
				currentModule = mrepo.save(currentModule);
			    return new ResponseEntity<Module>(currentModule, HttpStatus.OK);
		
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping("/modules/{code}/sessions")
	public ResponseEntity<?>createSessionInModule(@RequestBody Session sessions, UriComponentsBuilder ucBuilder, @PathVariable("code") String code) {
		 
		Module module = mrepo.findById(code).orElse(null);
		Session session = srepo.findById(sessions.getId()).orElse(null);
		if(module == null) {
			return new ResponseEntity(new ErrorInfo ("Module with code " + code + " not found"), HttpStatus.NOT_FOUND);
		
		}
		
		if(session != null) {
			return new ResponseEntity(new ErrorInfo ("Session with id " + sessions.getId() + " already exists"), HttpStatus.CONFLICT);	
		}
		
						module.getSessions().add(sessions);
		//				sessions = srepo.save(sessions);
						module = mrepo.save(module);
						
						HttpHeaders headers = new HttpHeaders();
						headers.setLocation(ucBuilder.path("/modules/{code}/sessions").buildAndExpand(module.getSessions()).toUri());
						return new ResponseEntity<String>(headers, HttpStatus.CREATED);
	}
					
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PutMapping("/modules/{code}/sessions/{id}")
	public ResponseEntity<?> updateSessionInModule(@RequestBody Session sessions, @PathVariable("code") String code, @PathVariable("id") Long id){
		
		Module module = mrepo.findById(code).orElse(null);
		Session session = srepo.findById(sessions.getId()).orElse(null);
					if(module == null) {
						return new ResponseEntity(new ErrorInfo ("Module with code " + code + " not found"), HttpStatus.NOT_FOUND);
					}
					
					if(session == null) {
						return new ResponseEntity(new ErrorInfo("Sesion with id " + sessions.getId() + " not found"), HttpStatus.NOT_FOUND);
					}
					
					
					
						Session currentSession = srepo.findById(id).orElse(null);
						module.getSessions().remove(currentSession);
						currentSession.setDatetime(sessions.getDatetime());
						currentSession.setDuration(sessions.getDuration());
						currentSession.setTopic(sessions.getTopic());
						module.getSessions().add(currentSession);
						module = mrepo.save(module);
						return new ResponseEntity<List>(module.getSessions(), HttpStatus.OK);
						
						}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@DeleteMapping("/modules/{code}")
	public ResponseEntity<?> deleteModule(@PathVariable("code") String code){
		Module module = mrepo.findById(code).orElse(null);
		List<Module> modstoDelete = new ArrayList<>();
		if (module == null) {
			return new ResponseEntity(new ErrorInfo("Module with code " + code + " not found"), HttpStatus.NOT_FOUND);
		}
		
		/// disassociate relationship between convenor and module before deleting module sessions, and then module entity. 
		for (Convenor c : crepo.findAll()) {
			for(Module m : c.getModules()) {
				if(m.getCode() == module.getCode()) {
					modstoDelete.add(m);
				}
			}
			c.getModules().removeAll(modstoDelete);
		}
		
		module.setSessions(null);
		mrepo.deleteById(code);
		
		List<Module> modules = mrepo.findAll();
		return new ResponseEntity<List>(modules, HttpStatus.OK);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@DeleteMapping("/modules/{code}/sessions/{id}")
	public ResponseEntity<?> deleteSessionInModule(@PathVariable("code") String code, @PathVariable("id") Long id){
		Module module = mrepo.findById(code).orElse(null);
		Session session = srepo.findById(id).orElse(null);
		
		if (module == null) {
			return new ResponseEntity(new ErrorInfo ("Module with code " + code + " not found"), HttpStatus.NOT_FOUND);
		}
		
		if(session == null) {
			return new ResponseEntity(new ErrorInfo("Session with id " + session.getId() + " not found"), HttpStatus.NOT_FOUND);
		}
		
	
		
			srepo.deleteById(session.getId());
			

		return new ResponseEntity<Module>(module, HttpStatus.OK);
	}
	
}
