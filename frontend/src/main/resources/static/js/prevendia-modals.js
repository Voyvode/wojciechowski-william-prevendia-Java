document.addEventListener('DOMContentLoaded', function () {
	// Configuration des boutons
	document.querySelector('.btn-new-patient').addEventListener('click', showAddPatientModal);

	document.querySelectorAll('.btn-patient-update').forEach(button => {
		button.addEventListener('click', showUpdatePatientModal);
	});

	document.querySelectorAll('.btn-patient-notes').forEach(button => {
		button.addEventListener('click', showPatientNotesModal);
	});
});

function showAddPatientModal() {
	fetch('/patients/add')
		.then(response => response.ok ? response.text() : Promise.reject("Erreur HTTP"))
		.then(html => {
			document.getElementById('dynamicModalContainer').innerHTML = html;
			new bootstrap.Modal(document.getElementById('addPatientModal')).show();
		})
		.catch(error => console.error("Erreur :", error));
}

function showUpdatePatientModal() {
	const id = this.closest('tr').dataset.patientId;
	fetch(`/patients/${id}/update`)
		.then(response => response.ok ? response.text() : Promise.reject("Erreur HTTP"))
		.then(html => {
			document.getElementById('dynamicModalContainer').innerHTML = html;
			new bootstrap.Modal(document.getElementById('updatePatientModal')).show();
		})
		.catch(error => console.error("Erreur :", error));
}

function showPatientNotesModal() {
	const id = this.closest('tr').dataset.patientId;
	fetch(`/patients/${id}/notes`)
		.then(response => response.ok ? response.text() : Promise.reject("Erreur HTTP"))
		.then(html => {
			document.getElementById('dynamicModalContainer').innerHTML = html;
			const modal = new bootstrap.Modal(document.getElementById('patientNotesModal'));
			modal.show();
			
			// Configuration du bouton d'ajout de note
			setupAddNoteButton(id);
		})
		.catch(error => console.error("Erreur :", error));
}

function setupAddNoteButton(patientId) {
	// Configuration du bouton pour afficher le formulaire
	const toggleNoteBtn = document.getElementById('toggle-note-btn');
	if (toggleNoteBtn) {
		toggleNoteBtn.onclick = function() {
			const form = document.getElementById('patientNotesForm');
			form.style.display = 'block';
			this.style.display = 'none';
			document.getElementById('new-note').focus();
		};
	}

	// Configuration du formulaire d'ajout de note
	const noteForm = document.getElementById('patientNotesForm');
	if (noteForm) {
		noteForm.addEventListener('submit', function(e) {
			e.preventDefault();
			submitNewNote(e, patientId);
		});
	}
}

function submitNewNote(event, patientId) {
	const form = event.target;
	const formData = new FormData(form);
	
	// S'assurer que l'ID du patient est correctement défini
	formData.set('patId', patientId);
	
	// Afficher les données du formulaire pour le débogage
	console.log('Données du formulaire:', Object.fromEntries(formData));
	
	// Désactiver le bouton de soumission pendant l'envoi
	const submitButton = form.querySelector('button[type="submit"]');
	const originalText = submitButton ? submitButton.innerHTML : '';
	
	if (submitButton) {
		submitButton.disabled = true;
		submitButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Enregistrement...';
	}

	// Envoyer la note au serveur au format FormData
	fetch(form.action, {
		method: 'POST',
		body: formData
	})
	.then(response => {
		if (!response.ok) {
			// Essayer de récupérer le corps de la réponse pour plus de détails
			return response.text().then(text => {
				throw new Error(`Erreur HTTP : ${response.status}. Détails : ${text}`);
			});
		}
		return response.text();
	})
	.then(() => {
		// Réinitialiser le formulaire
		form.reset();
		form.style.display = 'none';
		
		// Réafficher le bouton d'ajout
		const toggleNoteBtn = document.getElementById('toggle-note-btn');
		if (toggleNoteBtn) {
			toggleNoteBtn.style.display = 'block';
		}
		
		// Rafraîchir la liste des notes
		return refreshNotesList(patientId);
	})
	.then(() => {
		// Afficher un message de succès
		showAlert('Note enregistrée avec succès', 'success');
	})
	.catch(error => {
		console.error('Erreur lors de l\'envoi de la note:', error);
		showAlert(`Erreur lors de l'enregistrement de la note: ${error.message}`, 'danger');
	})
	.finally(() => {
		// Réactiver le bouton de soumission
		if (submitButton) {
			submitButton.disabled = false;
			submitButton.innerHTML = originalText;
		}
	});
}

function refreshNotesList(patientId) {
	return fetch(`/patients/${patientId}/notes`)
		.then(response => {
			if (!response.ok) {
				throw new Error(`Erreur HTTP lors de la mise à jour: ${response.status}`);
			}
			return response.text();
		})
		.then(html => {
			// Créer un conteneur temporaire pour analyser le HTML
			const tempDiv = document.createElement('div');
			tempDiv.innerHTML = html;

			// Récupérer la liste des notes mise à jour
			const newNotesContainer = tempDiv.querySelector('.modal-body');
			const currentNotesContainer = document.querySelector('.modal-body');

			if (newNotesContainer && currentNotesContainer) {
				// Sauvegarder le formulaire et le bouton d'ajout
				const form = document.getElementById('patientNotesForm');
				const toggleBtn = document.getElementById('toggle-note-btn');
				
				// Remplacer uniquement le contenu de la liste des notes
				currentNotesContainer.innerHTML = newNotesContainer.innerHTML;
				
				// Réappliquer les événements au formulaire et au bouton
				setupAddNoteButton(patientId);
			}
		});
}

function showAlert(message, type) {
	const alertDiv = document.createElement('div');
	alertDiv.className = `alert alert-${type}`;
	alertDiv.textContent = message;
	document.querySelector('.modal-body').prepend(alertDiv);
	setTimeout(() => alertDiv.remove(), 3000);
}
