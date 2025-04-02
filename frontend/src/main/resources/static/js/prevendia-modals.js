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

			document.getElementById('toggle-note-btn').onclick = function () {
				const form = document.getElementById('patientNotesForm');
				form.style.display = 'block';
				this.style.display = 'none';
				document.getElementById('new-note').focus();
			};

			// Intercepter la soumission du formulaire
			document.getElementById('patientNotesForm').addEventListener('submit', function (e) {
				e.preventDefault();

				// Stocker la référence au formulaire pour l'utiliser dans les callbacks
				const form = this;

				// Gérer le bouton de soumission s'il existe
				let submitButton = form.querySelector('button[type="submit"]');
				let originalText = '';

				if (submitButton) {
					originalText = submitButton.innerHTML;
					submitButton.disabled = true;
					submitButton.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Enregistrement...';
				}

				fetch(form.action, {
					method: 'POST',
					body: new FormData(form)
				})
					.then(response => {
						if (response.ok) {
							// Créer une fonction pour réinitialiser l'état du bouton
							const resetButton = () => {
								if (submitButton) {
									submitButton.disabled = false;
									submitButton.innerHTML = originalText;
								}
							};

							// Réinitialiser le champ de texte
							const noteInput = document.getElementById('new-note');
							if (noteInput) {
								noteInput.value = '';
							}

							// Récupérer uniquement les notes mises à jour sans toucher à la modale
							fetch(`/patients/${id}/notes`)
								.then(response => response.ok ? response.text() : Promise.reject("Erreur HTTP"))
								.then(html => {
									// Créer un conteneur temporaire pour analyser le HTML
									const tempDiv = document.createElement('div');
									tempDiv.innerHTML = html;

									// Rechercher l'élément qui contient la liste des notes dans la nouvelle réponse
									const newNotesContainer = tempDiv.querySelector(
										'[data-role="notes-container"], .notes-list, #notesList, .patient-notes, #patient-notes'
									) || tempDiv.querySelector('.modal-body');

									if (newNotesContainer) {
										// Trouver le même élément dans la modale actuelle
										const currentNotesContainer = document.querySelector(
											'[data-role="notes-container"], .notes-list, #notesList, .patient-notes, #patient-notes'
										) || document.querySelector('.modal-body');

										if (currentNotesContainer) {
											// Remplacer uniquement le contenu de la liste des notes
											currentNotesContainer.innerHTML = newNotesContainer.innerHTML;

											// Reconfigurer le bouton d'ajout de notes
											const toggleNoteBtn = document.getElementById('toggle-note-btn');
											if (toggleNoteBtn) {
												toggleNoteBtn.style.display = 'block';
												toggleNoteBtn.onclick = function () {
													const form = document.getElementById('patientNotesForm');
													form.style.display = 'block';
													this.style.display = 'none';
													document.getElementById('new-note').focus();
												};
											}

											// Cacher à nouveau le formulaire
											const noteForm = document.getElementById('patientNotesForm');
											if (noteForm) {
												noteForm.style.display = 'none';
												noteForm.addEventListener('submit', arguments.callee);
											}

											// Réinitialiser le bouton
											resetButton();

											// Afficher un message de confirmation
											const alertDiv = document.createElement('div');
											alertDiv.className = 'alert alert-success';
											alertDiv.textContent = 'Note enregistrée avec succès';
											currentNotesContainer.prepend(alertDiv);
											setTimeout(() => alertDiv.remove(), 3000);
										} else {
											console.error("Élément contenant les notes non trouvé dans la modale actuelle");
											resetButton();
										}
									} else {
										console.error("Élément contenant les notes non trouvé dans la réponse");
										resetButton();
									}
								})
								.catch(error => {
									console.error("Erreur lors de la mise à jour des notes :", error);
									resetButton();
								});
						} else {
							// Réinitialiser le bouton
							if (submitButton) {
								submitButton.disabled = false;
								submitButton.innerHTML = originalText;
							}
						}
					})
					.catch(error => {
						console.error("Erreur lors de l'envoi de la note :", error);
						if (submitButton) {
							submitButton.disabled = false;
							submitButton.innerHTML = originalText;
						}
					});
			});
		})
		.catch(error => console.error("Erreur :", error));
}
