package com.ista.isp.assessment.todo;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/Todo")
public class TodoServices {

	ArrayList<TodoItem> items = new ArrayList<TodoItem>();
	int nextId = 0;
	
	
	
	/*
	 * Generate a few items just for testing
	 */
	@GetMapping("/generateItems")
	public void generateItems() {
		
		for(int i = 0; i < 5; i++) {
			
			TodoItem item = new TodoItem();
			
			item.setId(nextId++);
			item.setName("Example task name");
			item.setDescription("Short description");
			item.setStatus("Pending");
			item.setPriority(5-i);
			
			items.add(item.getId(), item);
		}
	}
	
	
	/*
	 * Return all the items
	 * 
	 */
	@GetMapping("/getItems")
	public ResponseEntity<ArrayList<TodoItem>> getItems(){
		
		return ResponseEntity.ok().body(items);
	}
	
	/*
	 * Return all the items in course (status not in "checked")
	 * 
	 */
	@GetMapping("/getPendingItems")
	public ResponseEntity<ArrayList<TodoItem>> getPendingItems (){
		
		ArrayList<TodoItem> pendingItems = new ArrayList<TodoItem>();
		try {
			
			for (TodoItem item : items) {
				if(item.getStatus().compareToIgnoreCase("checked") != 0) {
					pendingItems.add(item);
				}
			}
			
			return ResponseEntity.ok().body(pendingItems);
		} catch (Exception e) {
			
			return ResponseEntity.notFound().build();
		}
	}
	
	/*
	 * Returns an item filtering by id (unique)
	 * 
	 */
	@PostMapping("/getItemById")
	public ResponseEntity<TodoItem> getItemById (@RequestBody int id) {
		try {
			
			return ResponseEntity.ok().body(items.get(id));
		} catch (Exception e) {
			
			return new ResponseEntity<TodoItem>(HttpStatus.BAD_REQUEST);
		}	
	}
	
	/*
	 * Receive an item body without the id and create the new item with the next id available.
	 * 
	 * Returns the created item.
	 */
	@PostMapping("/crearItem")
	public ResponseEntity<TodoItem> createItem (@RequestBody TodoItem item) {
		try {
			item.setId(nextId++);
			items.add(item);
			
			return ResponseEntity.ok().body(item);
		} catch (Exception e) {
			
			return ResponseEntity.notFound().build();
		}
	}
	
	
	/*
	 * Receive an existing item body with different values.
	 * Check that the item exist (by id), create a copy, rewrite the values, 
	 * remove the older one and create the new one.
	 * 
	 * Return the updated item.
	 */
	@PostMapping("/updateItem")
	public ResponseEntity<TodoItem> updateItem (@RequestBody TodoItem item ){
		if(item.getId() < nextId) {
			try {
				TodoItem item2update;
				item2update = items.get(item.getId());
				
				item2update.setName(item.getName());
				item2update.setDescription(item.getDescription());
				item2update.setStatus(item.getStatus());
				item2update.setPriority(item.getPriority());
				
				items.remove(item.getId());
				items.add(item.getId(), item2update);
				
				return ResponseEntity.ok().body(item2update);
			} catch (Exception e) {
				return ResponseEntity.notFound().build();
			}
		}else {
			return ResponseEntity.badRequest().build();
		}
	}
	
	/*
	 * Receive an id and remove the associated item.
	 * 
	 * Return an String with the ack.
	 * 
	 * IMPORTANT: There is a problem, remove() from ArrrayList automatically shifts the array and its broke 
	 * the relation index = id. Using a data base instead of an ArrayList for testing should solve the problem.
	 */
	@PostMapping("/deleteItem")
	public ResponseEntity<String> deleteItem (@RequestBody int id) {
		try {
			
			items.remove(id);
			
			return ResponseEntity.ok().body("Item with id: "+id+" deleted");
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

}
