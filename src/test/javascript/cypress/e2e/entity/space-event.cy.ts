import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('SpaceEvent e2e test', () => {
  const spaceEventPageUrl = '/space-event';
  const spaceEventPageUrlPattern = new RegExp('/space-event(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const spaceEventSample = {
    name: 'hm notwithstanding',
    date: '2025-03-19',
    description: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
    photo: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=',
    photoContentType: 'unknown',
    type: 'LANDING',
  };

  let spaceEvent;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/space-events+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/space-events').as('postEntityRequest');
    cy.intercept('DELETE', '/api/space-events/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (spaceEvent) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/space-events/${spaceEvent.id}`,
      }).then(() => {
        spaceEvent = undefined;
      });
    }
  });

  it('SpaceEvents menu should load SpaceEvents page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('space-event');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SpaceEvent').should('exist');
    cy.url().should('match', spaceEventPageUrlPattern);
  });

  describe('SpaceEvent page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(spaceEventPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SpaceEvent page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/space-event/new$'));
        cy.getEntityCreateUpdateHeading('SpaceEvent');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', spaceEventPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/space-events',
          body: spaceEventSample,
        }).then(({ body }) => {
          spaceEvent = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/space-events+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/space-events?page=0&size=20>; rel="last",<http://localhost/api/space-events?page=0&size=20>; rel="first"',
              },
              body: [spaceEvent],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(spaceEventPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details SpaceEvent page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('spaceEvent');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', spaceEventPageUrlPattern);
      });

      it('edit button click should load edit SpaceEvent page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SpaceEvent');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', spaceEventPageUrlPattern);
      });

      it('edit button click should load edit SpaceEvent page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SpaceEvent');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', spaceEventPageUrlPattern);
      });

      it('last delete button click should delete instance of SpaceEvent', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('spaceEvent').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', spaceEventPageUrlPattern);

        spaceEvent = undefined;
      });
    });
  });

  describe('new SpaceEvent page', () => {
    beforeEach(() => {
      cy.visit(`${spaceEventPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SpaceEvent');
    });

    it('should create an instance of SpaceEvent', () => {
      cy.get(`[data-cy="name"]`).type('nor');
      cy.get(`[data-cy="name"]`).should('have.value', 'nor');

      cy.get(`[data-cy="date"]`).type('2025-03-20');
      cy.get(`[data-cy="date"]`).blur();
      cy.get(`[data-cy="date"]`).should('have.value', '2025-03-20');

      cy.get(`[data-cy="description"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="description"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.setFieldImageAsBytesOfEntity('photo', 'integration-test.png', 'image/png');

      cy.get(`[data-cy="type"]`).select('LAUNCH');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        spaceEvent = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', spaceEventPageUrlPattern);
    });
  });
});
