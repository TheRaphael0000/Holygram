package ch.hearc.holygram.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;

/*
 * Dirty method to get attributes of a user 3 Classes (Template, Business,
 * Entity) for each Model should be used
 */
@Entity
public class Exorcist {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn
	@JsonBackReference
	private User user;

	@NotNull
	@Size(min = 1, max = 1000)
	private String description;

	@NotNull
	private String phoneNumber;

	@ManyToOne
	@JoinColumn
	@JsonBackReference
	private Canton canton;

	@OneToMany(mappedBy = "exorcist", cascade = CascadeType.ALL)
	@JsonBackReference
	private List<Evaluation> evaluations;

	@OneToMany(mappedBy = "exorcist", cascade = CascadeType.ALL)
	@JsonBackReference
	private List<Service> services;

	public Exorcist(User user, String description, String phoneNumber, Canton canton) {
		this.user = user;
		this.description = description;
		this.phoneNumber = phoneNumber;
		this.canton = canton;
		this.evaluations = new ArrayList<>();
		this.services = new ArrayList<>();
	}

	public Exorcist() {
	}

	public User getUser() {
		return user;
	}

	public synchronized void setUser(User user) {
		this.user = user;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Canton getCanton() {
		return canton;
	}

	public void setCanton(Canton canton) {
		this.canton = canton;
	}

	public List<Evaluation> getEvaluations() {
		Collections.sort(evaluations, (o1, o2) -> -o1.getDatetime().compareTo(o2.getDatetime()));
		return evaluations;
	}

	public void setEvaluations(List<Evaluation> evaluations) {
		this.evaluations = evaluations;
	}

	public List<Service> getServices() {
		Collections.sort(services, (s1, s2) -> Float.compare(s1.getPrice(), s2.getPrice()));
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public int getRenown() {
		// Get renown
		List<Evaluation> eEvaluations = this.getEvaluations();
		long eEvaluationsPositives = eEvaluations.parallelStream().filter(x -> x.isPositive()).count();
		float renown = eEvaluationsPositives / (eEvaluations.size() * 1.0f);
		return (int) (renown * 5);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
