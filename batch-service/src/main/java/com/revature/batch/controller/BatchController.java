package com.revature.batch.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.batch.bean.Batch;
import com.revature.batch.bean.BatchType;
import com.revature.batch.exception.BatchException;
import com.revature.batch.exception.NoBatchException;
import com.revature.batch.service.BatchService;
import com.revature.batch.service.TrainerService;

@RestController
@RequestMapping(value = "/api/v2/Batch/")
public class BatchController {

	@Autowired
	TrainerService trainerService;

	@Autowired
	BatchService batchService;

	/**
	 * A method to get all batches using BatchService.
	 * 
	 * @author Zakary S. Winston R.
	 * 
	 * @return a list of all batches, Http status 200 otherwise Http status 204
	 * @throws NoBatchException 
	 */
	@GetMapping("all")
	public ResponseEntity<List<Batch>> getBatchAll() throws NoBatchException {
		List<Batch> result = batchService.getBatchAll();

		if (result == null || result.isEmpty()) {
			//return new ResponseEntity<List<Batch>>(HttpStatus.NO_CONTENT);
			throw new NoBatchException("No Batch Found", HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Batch>>(result, HttpStatus.OK);
	}

	/**
	 * A method to get all past batches for the trainer using BatchService.
	 *
	 * @author Zakary S. Winston R.
	 * 
	 * @param request
	 *            Http request hold the trainer email as parameter.
	 * @return a list of all past batches for the trainer, Http status 200 otherwise
	 *         Http status 204
	 * @throws NoBatchException 
	 */
	@GetMapping("past/{email}")
	public ResponseEntity<List<Batch>> getPastBatches(@PathVariable String email) throws NoBatchException {
		List<Batch> batches = batchService.getBatchByTrainerID(trainerService.getTrainerByEmail(email));
		
		// List<Batch> pastBatches = new ArrayList<>();
		// for (Batch b : batches) {
		// if (new Timestamp(System.currentTimeMillis()).after(b.getEndDate())) {
		// pastBatches.add(b);
		// }
		// }
		// return pastBatches;
		Timestamp t = new Timestamp(System.currentTimeMillis());
		batches.removeIf(b -> t.before(b.getEndDate()));
		if (batches.isEmpty()) {
//			return new ResponseEntity<List<Batch>>(HttpStatus.NO_CONTENT);
			throw new NoBatchException("No Batch Found", HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Batch>>(batches, HttpStatus.OK);
	}

	/**
	 * A method to get all future batches for the trainer using BatchService.
	 * 
	 * @author Zakary S. Winston R.
	 * 
	 * @param request
	 *            Http request hold the trainer email as parameter.
	 * @return a list of all future batches for the trainer, Http status 200
	 *         otherwise Http status 204
	 * @throws NoBatchException 
	 */
	@GetMapping("future/{email}")
	public ResponseEntity<List<Batch>> getFutureBatches(@PathVariable String email) throws NoBatchException {
		List<Batch> batches = batchService.getBatchByTrainerID(trainerService.getTrainerByEmail(email));

		// List<Batch> futureBatches = new ArrayList<>();
		// for (Batch b : batches) {
		// if (new Timestamp(System.currentTimeMillis()).before(b.getStartDate())) {
		// futureBatches.add(b);
		// }
		// }
		// return futureBatches;
		Timestamp t = new Timestamp(System.currentTimeMillis());
		batches.removeIf(b -> t.after(b.getStartDate()));
		if (batches.isEmpty()) {
//			return new ResponseEntity<List<Batch>>(HttpStatus.NO_CONTENT);
			throw new NoBatchException("No Batch Found", HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Batch>>(batches, HttpStatus.OK);
	}

	/**
	 * A method to get all in-progress batches for the trainer using BatchService.
	 * 
	 * @author Zakary S. Winston R.
	 * 
	 * @param request
	 *            Http request hold the trainer email as parameter.
	 * @return a list of all in-progress batches for the trainer, Http status 200
	 *         otherwise Http status 204
	 * @throws NoBatchException 
	 */
	@GetMapping("inprogress/{email}")
	public ResponseEntity<Batch> getBatchInProgress(@PathVariable String email) throws NoBatchException {
		List<Batch> batches = batchService.getBatchByTrainerID(trainerService.getTrainerByEmail(email));

		Batch batchInProgress = null;
		Timestamp t = new Timestamp(System.currentTimeMillis());
		for (Batch b : batches) {
			if (t.after(b.getStartDate()) && t.before(b.getEndDate())) {
				batchInProgress = b;
				break;
			}
		}
		if (batchInProgress == null) {
//			return new ResponseEntity<Batch>(HttpStatus.NO_CONTENT);
			throw new NoBatchException("No Batch Found", HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Batch>(batchInProgress, HttpStatus.OK);
	}

	/**
	 * A method to get all in-progress for the trainer batches using BatchService.
	 * 
	 * @author Zakary S. Winston R.
	 * 
	 * @param request
	 *            Http request hold the trainer email as parameter.
	 * @return a list of all in-progress batches for the trainer, Http status 200
	 *         otherwise Http status 204
	 * @throws NoBatchException 
	 */
	@GetMapping("allinprogress/{email}")
	public ResponseEntity<List<Batch>> getAllBatchesInProgress(@PathVariable String email) throws NoBatchException {
		
		List<Batch> batches = batchService.getBatchByTrainerID(trainerService.getTrainerByEmail(email));

		// List<Batch> batchesInProgress = new ArrayList<>();
		// Timestamp t = new Timestamp(System.currentTimeMillis());
		// for (Batch b : batches) {
		// if (t.after(b.getStartDate()) && t.before(b.getEndDate())) {
		// batchesInProgress.add(b);
		// }
		// }
		// return batchesInProgress;

		Timestamp t = new Timestamp(System.currentTimeMillis());
		batches.removeIf(b -> t.before(b.getStartDate()) || t.after(b.getEndDate()));
		if (batches.isEmpty()) {
//			return new ResponseEntity<List<Batch>>(HttpStatus.NO_CONTENT);
			throw new NoBatchException("No Batches in progress", HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Batch>>(batches, HttpStatus.OK);
	}

	// TODO look up jackson exception for spring mvc @RequestBody Type for parameter
	// input
//	@RequestMapping(value = "Edit", method = RequestMethod.POST, produces = "application/json")
//	public void updateUser(@RequestBody String jsonObject) {
//		Batch currentBatch = null;
//		try {
//			currentBatch = new ObjectMapper().readValue(jsonObject, Batch.class);
//			batchService.addOrUpdateBatch(currentBatch);
//		} catch (IOException e) {
//			LogManager.getRootLogger().error(e);
//		}
//	}

	/**
	 * A method to get batch by batch id using BatchService.
	 * 
	 * @author Zakary S. Winston R.
	 * 
	 * @param request
	 *            Http request hold the batch id as parameter.
	 * @return a batch , Http status 200 otherwise Http status 204.
	 * @throws NoBatchException 
	 */
	@GetMapping("byid/{batchId}")
	public ResponseEntity<Batch> getBatchById(@PathVariable int batchId) throws NoBatchException {
		Batch result = batchService.getBatchById(batchId);
		if (result == null) {
//			return new ResponseEntity<Batch>(HttpStatus.NO_CONTENT);
			throw new NoBatchException("No Batch Found", HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Batch>(result, HttpStatus.OK);
	}

	/**
	 * A method to update batch using BatchService.
	 * 
	 * @author Zakary S. Winston R.
	 * 
	 * @param batch
	 *            to be update.
	 * @return batch and Http status 202 otherwise Http status 400
	 * @throws BatchException 
	 */
	@PostMapping("updatebatch")
	public ResponseEntity<Batch> updateBatch(@RequestBody Batch batch) throws BatchException {
		System.out.println(batch);
		
		Batch result = batchService.addOrUpdateBatch(batch);
		if (result == null) {
//			return new ResponseEntity<Batch>(HttpStatus.BAD_REQUEST);
			throw new BatchException("Error processing operation on batch", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Batch>(result, HttpStatus.ACCEPTED);

	}

	/**
	 * A method to get all batch types using BatchService.
	 * 
	 * @author Zakary S. Winston R.
	 * 
	 * @return a list of batch types, Http status 200 otherwise Http status 204
	 * @throws NoBatchException 
	 */
	@GetMapping("batchtypes")
	public ResponseEntity<List<BatchType>> getAllBatchTypes() throws NoBatchException {
		List<BatchType> result = batchService.getAllBatchTypes();
		if (result == null || result.isEmpty()) {
//			return new ResponseEntity<List<BatchType>>(HttpStatus.NO_CONTENT);
			throw new NoBatchException("No Batch Found", HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<BatchType>>(result, HttpStatus.OK);
	}

	/**
	 * Method to get all currently active batches
	 * 
	 * @author Francisco Palomino | Batch: 1712-dec10-java-steve | Zakary S., Winston R.
	 * @return a list of batches, Http status 200 otherwise Http status 204
	 * @throws NoBatchException 
	 */
	@GetMapping("currentbatches")
	public ResponseEntity<List<Batch>> getAllInProgress() throws NoBatchException {
		List<Batch> batchesInProgress = batchService.currentBatches();
		if (batchesInProgress == null || batchesInProgress.isEmpty()) {
//			return new ResponseEntity<List<Batch>>(HttpStatus.NO_CONTENT);
			throw new NoBatchException("No batches in progress", HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<Batch>>(batchesInProgress, HttpStatus.OK);
	}

}
