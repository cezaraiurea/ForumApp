describe('template spec', () => {
  it('passes', () => {
    cy.visit('http://localhost:3000');
    cy.get('[href="/login"]').click();
    cy.get('[placeholder="Email"]').type('cezara12@gmail.com');
    cy.get('[placeholder="Parolă"]').type('parola123');
    cy.wait(1000);
    cy.get('button').click();
    cy.wait(1000);
    cy.get('.big-post-btn').click();
    cy.wait(1000);
    cy.get('[placeholder="Titlu întrebare"]').type('Test123');
    cy.get('textarea').type('Acum avem e2e testing!!!');
    cy.get('[placeholder="Etichete separate prin virgulă (ex: java, spring)"]').type('nota10!');
    cy.get('form > button').click();
    cy.wait(1000);
    cy.get(':nth-child(1) > .see-details-btn').click();
  })
})