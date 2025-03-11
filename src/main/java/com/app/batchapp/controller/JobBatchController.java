package com.app.batchapp.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.batchapp.dto.JobBatchForm;
import com.app.batchapp.model.JobBatch;
import com.app.batchapp.model.JobStatus;
import com.app.batchapp.services.JobBatchService;

@Controller
@RequestMapping("/jobBatchs")
public class JobBatchController {

	private static final Logger log = (Logger) LoggerFactory.getLogger(JobBatchController.class);
	public static final String moduleName = "jobatchs";
	@Autowired
	private JobBatchService<JobBatch> jobBatchService;

	public JobBatchController(final JobBatchService<JobBatch> jobBatchService) {
		this.jobBatchService = jobBatchService;
	}

	// handler method to handle list oldObjects and return mode and view
	@GetMapping({ "", "/home", "/index" })
	public String index(Model model, @RequestParam(required = false, defaultValue = "") String keyword,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "6") int size) {

		try {
			List<JobBatch> jobBatchs = new ArrayList<JobBatch>();
			Pageable paging = PageRequest.of(page - 1, size);

			Page<JobBatch> pageTuts;
			if (keyword == null) {
				pageTuts = jobBatchService.findAll(paging);
			} else {
				pageTuts = jobBatchService.findByNameContainingIgnoreCasePage(keyword, paging);
				model.addAttribute("keyword", keyword);
			}

			jobBatchs = pageTuts.getContent();

			model.addAttribute("list", jobBatchs);
			model.addAttribute("moduleName", moduleName);
			model.addAttribute("currentPage", pageTuts.getNumber() + 1);
			model.addAttribute("totalItems", pageTuts.getTotalElements());
			model.addAttribute("totalPages", pageTuts.getTotalPages());
			model.addAttribute("pageSize", size);
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
		}

		return "jobatchs/list";
	}

	@GetMapping("/show")

	// public String showJobBatchForm(@PathVariable Long id, Model model)
	public String showJobBatchForm(Model model, @RequestParam(required = true) long id,
			@RequestParam(required = false, defaultValue = "") String keyword,
			@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "size", defaultValue = "6") int size) {

		log.info("passage from index to show page ");
		model.addAttribute("jobBatch", convertToForm(jobBatchService.getJobBatchById(id)));

		model.addAttribute("moduleName", moduleName);
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		return "jobatchs/show";
	}

	@GetMapping("/new")
	public String newJobBatch(Model model) {
		JobBatch jobBatch = new JobBatch();

		model.addAttribute("jobBatch", jobBatch);
		model.addAttribute("pageTitle", "Create new JobBatch");

		return "jobatchs/form";
	}

	@PostMapping("/save")
	public String saveJobBatch(JobBatch jobBatch, RedirectAttributes redirectAttributes) {
		try {
			jobBatchService.saveJobBatch(jobBatch);

			redirectAttributes.addFlashAttribute("message", "The JobBatch has been saved successfully!");
		} catch (Exception e) {
			redirectAttributes.addAttribute("message", e.getMessage());
		}

		return "redirect:/jobBatchs";
	}

	@GetMapping("/{id}")
	public String editJobBatch(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
		try {
			JobBatch jobBatch = jobBatchService.getJobBatchById(id);

			model.addAttribute("jobBatch", jobBatch);
			model.addAttribute("pageTitle", "Edit JobBatch (ID: " + id + ")");

			return "jobatchs/form";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());

			return "redirect:/jobatchs";
		}
	}

	@GetMapping("/{id}/available/{status}")
	public String updateTutorialPublishedStatus(@PathVariable("id") long id, @PathVariable("status") boolean available,
			Model model, RedirectAttributes redirectAttributes) {
		try {
			jobBatchService.updateAvailableStatus(id, available);

			String status = available ? "available" : "disabled";
			String message = "The jobBatch id=" + id + " has been " + status;

			redirectAttributes.addFlashAttribute("message", message);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}

		return "redirect:/jobBatchs";
	}

	@GetMapping("/add")
	public String createJobBatchForm(Model model) {

		log.info("passage from index to add page ");

		// create oldObject object to hold oldObject form data
		JobBatchForm jobBatch = new JobBatchForm();
		model.addAttribute("jobBatch", jobBatch);
		return "jobatchs/form";

	}

	@PostMapping("/add")
	public String saveJobBatch(@ModelAttribute("jobBatch") JobBatchForm oldObject,
			@RequestParam(value = "paramKey[]") List<String> paramKeys,
			@RequestParam(value = "paramValue[]") List<String> paramValues) throws JobExecutionAlreadyRunningException,
			JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {

		log.info("passage from index to post action :save ");
		JobBatch newObject = new JobBatch();
		newObject = convertForm(oldObject);

		for (int i = 0; i < paramKeys.size(); i++) {
			newObject.addParameter(paramKeys.get(i), paramValues.get(i));
		}

		// this.setCurrentJobBatch(newObject);
		log.info("passage from index to post action :save ");
		// Update status to "PROCESSING"
		newObject.setStatus(JobStatus.STARTED);
		// jobBatchService.updateStatus(currentJobBatch.getId(), JobStatus.PROCESSING);
		jobBatchService.saveJobBatch(newObject);
		// return this.handleFileUpload();

		return "redirect:/jobBatchs";
	}

	@GetMapping("/edit/{id}")
	public String editJobBatchForm(@PathVariable Long id, Model model) {

		log.info("passage from index to edit page ");
		model.addAttribute("jobBatch", convertToForm(jobBatchService.getJobBatchById(id)));
		return "jobatchs/form";
	}

	@PostMapping("/{id}")
	public String updateJobBatch(@PathVariable Long id, @ModelAttribute("jobBatch") JobBatchForm oldObject,
			Model model) {

		// get oldObject from database by id
		JobBatch newObject = jobBatchService.getJobBatchById(id);

		newObject = convertForm(oldObject);
		newObject.setId(id);
		// save updated oldObject object
		jobBatchService.updateJobBatch(newObject);
		return "redirect:/jobatchs";
	}

	// handler method to handle delete oldObject request

	@DeleteMapping("/{id}")
	public String delJobBatch(@PathVariable Long id) {
		jobBatchService.deleteJobBatchById(id);
		return "redirect:/jobBatchs";
	}

	@PostMapping("delete/{id}")
	public String deleteJobBatch(@PathVariable Long id) {
		jobBatchService.deleteJobBatchById(id);
		return "redirect:/jobBatchs";
	}

	public JobBatch convertForm(JobBatchForm oldObject) {
		JobBatch newObject = new JobBatch();

		BeanUtils.copyProperties(oldObject, newObject);
		if (newObject == null)
			newObject = extracted(oldObject);
		return newObject;
	}

	public JobBatchForm convertToForm(JobBatch oldObject) {
		JobBatchForm newObject = new JobBatchForm();

		BeanUtils.copyProperties(oldObject, newObject);
		if (newObject == null)
			newObject = extractedToForm(oldObject);
		return newObject;
	}

	private JobBatch extracted(JobBatchForm oldObject) {
		JobBatch newObject = new JobBatch();

		// newObject.setId(oldObject.getId());
		newObject.setCode(oldObject.getCode());
		newObject.setName(oldObject.getName());
		newObject.setDescription(oldObject.getDescription());
		newObject.setAvailable(oldObject.isAvailable());
		newObject.setNbrAvailable(Long.valueOf(oldObject.getNbrAvailable()));
		newObject.setNbrFiles(Long.valueOf(oldObject.getNbrFiles()));
		newObject.setDescription(oldObject.getDescription());
		return newObject;
	}

	private JobBatchForm extractedToForm(JobBatch oldObject) {
		JobBatchForm newObject = new JobBatchForm();

		newObject.setId(oldObject.getId());
		newObject.setCode(oldObject.getCode());
		newObject.setName(oldObject.getName());
		newObject.setDescription(oldObject.getDescription());
		newObject.setAvailable(oldObject.isAvailable());
		newObject.setNbrAvailable(oldObject.getNbrAvailable());
		newObject.setNbrFiles(oldObject.getNbrFiles());
		newObject.setDescription(oldObject.getDescription());
		return newObject;
	}
}
