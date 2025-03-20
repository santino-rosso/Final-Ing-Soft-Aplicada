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

describe('Mission e2e test', () => {
  const missionPageUrl = '/mission';
  const missionPageUrlPattern = new RegExp('/mission(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const missionSample = { name: 'aw unimpressively finer' };

  let mission;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/missions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/missions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/missions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mission) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/missions/${mission.id}`,
      }).then(() => {
        mission = undefined;
      });
    }
  });

  it('Missions menu should load Missions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mission');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Mission').should('exist');
    cy.url().should('match', missionPageUrlPattern);
  });

  describe('Mission page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(missionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Mission page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mission/new$'));
        cy.getEntityCreateUpdateHeading('Mission');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', missionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/missions',
          body: missionSample,
        }).then(({ body }) => {
          mission = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/missions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/missions?page=0&size=20>; rel="last",<http://localhost/api/missions?page=0&size=20>; rel="first"',
              },
              body: [mission],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(missionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Mission page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mission');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', missionPageUrlPattern);
      });

      it('edit button click should load edit Mission page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Mission');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', missionPageUrlPattern);
      });

      it('edit button click should load edit Mission page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Mission');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', missionPageUrlPattern);
      });

      it('last delete button click should delete instance of Mission', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mission').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', missionPageUrlPattern);

        mission = undefined;
      });
    });
  });

  describe('new Mission page', () => {
    beforeEach(() => {
      cy.visit(`${missionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Mission');
    });

    it('should create an instance of Mission', () => {
      cy.get(`[data-cy="name"]`).type('sequester though');
      cy.get(`[data-cy="name"]`).should('have.value', 'sequester though');

      cy.get(`[data-cy="description"]`).type('league');
      cy.get(`[data-cy="description"]`).should('have.value', 'league');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mission = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', missionPageUrlPattern);
    });
  });
});
