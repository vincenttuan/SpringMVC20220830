package spring.mvc.session12.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import spring.mvc.session12.entity.Job;
import spring.mvc.session12.repository.EmployeeDao;
import spring.mvc.session12.repository.JobDao;

@Controller
@RequestMapping("/jdbc/job")
public class JobController {
	
	@Autowired
	private JobDao jobDao;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@GetMapping("/")
	public String index(@ModelAttribute Job job, Model model) {
		model.addAttribute("_method", "POST");
		model.addAttribute("jobs", jobDao.query());
		model.addAttribute("employees", employeeDao.query());
		return "session12/job";
	}
	
	@GetMapping("/{jid}")
	public String get(@PathVariable("jid") Integer jid, Model model) {
		model.addAttribute("_method", "PUT");
		model.addAttribute("jobs", jobDao.query());
		model.addAttribute("employees", employeeDao.query());
		model.addAttribute("job", jobDao.getById(jid));
		return "session12/job";
	}
	
	
	@PostMapping("/")
	public String add(@ModelAttribute @Valid Job job, BindingResult result, Model model) {
		if(result.hasErrors()) {
			model.addAttribute("_method", "POST");
			model.addAttribute("jobs", jobDao.query());
			model.addAttribute("employees", employeeDao.query());
			return "session12/job";
		}
		jobDao.add(job);
		return "redirect:./";
	}
	
	@PutMapping("/")
	public String update(@ModelAttribute @Valid Job job, BindingResult result, Model model) {
		if(result.hasErrors()) {
			model.addAttribute("_method", "POST");
			model.addAttribute("jobs", jobDao.query());
			model.addAttribute("employees", employeeDao.query());
			//model.addAttribute("Job", job); // 因為參數有設定 @ModelAttribute 所以系統會自動帶入, 因此就不用手動撰寫帶入 job
			return "session12/job";
		}
		jobDao.update(job);
		return "redirect:./";
	}
	
	
}