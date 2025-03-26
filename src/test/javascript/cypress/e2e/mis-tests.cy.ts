import { entityCreateButtonSelector, entityCreateSaveButtonSelector } from '../support/entity';

describe('Probando funcionalidades', () => {
  const exampleMission = {
    name: 'Misión de prueba',
    description: 'Descripción de la misión de prueba',
  };

  const exampleSpaceEvent = {
    name: 'Evento de prueba',
    date: '2025-03-08',
    description: 'Descripción del evento de prueba',
    photo: '/home/santino/Documentos/Final-Ing-Soft-Aplicada/src/test/javascript/cypress/fixtures/imagen_luna.jpeg',
    type: 'LAUNCH',
  };

  const exampleEditSpaceEvent = {
    name: 'Evento editado',
    date: '2020-02-02',
    description: 'Descripción editada del evento de prueba',
  };

  // Limpiar SessionStorage
  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });
  });

  // Inicio sesion y voy al inicio
  beforeEach(() => {
    cy.login('admin', 'admin');
    cy.visit('');
  });

  // Interceptar la peticion de creacion y obtencion de missions. Ademas de creacion, obtencion y modoficacion de space events.
  // Esto se hace para poder esperar a estas peticiones más adelante y verificar sus respuestas.
  beforeEach(() => {
    cy.intercept('GET', '/api/missions*').as('getEntitiesMissionsRequest');
    cy.intercept('POST', '/api/missions').as('postEntityMissionRequest');
    cy.intercept('GET', '/api/space-events*').as('getEntitiesSpaceEventsRequest');
    cy.intercept('POST', '/api/space-events').as('postEntitySpaceEventRequest');
    cy.intercept('PUT', /\/api\/space-events\/\d+$/).as('putEntitySpaceEventRequest');
  });

  it('Ir a la pagina de entidades de Mission y crear una nueva', () => {
    cy.clickOnEntityMenuItem('mission');
    cy.get(entityCreateButtonSelector).click();

    cy.get(`[data-cy="name"]`).type(exampleMission.name);
    cy.get(`[data-cy="description"]`).type(exampleMission.description);
    cy.get(entityCreateSaveButtonSelector).click();

    cy.wait('@postEntityMissionRequest').then(({ response }) => {
      expect(response?.statusCode).to.equal(201);
    });

    cy.wait('@getEntitiesMissionsRequest').then(({ response }) => {
      expect(response?.statusCode).to.equal(200);
    });
  });

  it('Ir a la pagina de entidades de Space Event y crear uno nuevo asociandolo a la mission creada anteriormente', () => {
    cy.clickOnEntityMenuItem('space-event');
    cy.get(entityCreateButtonSelector).click();

    cy.get(`[data-cy="name"]`).type(exampleSpaceEvent.name);
    cy.get(`[data-cy="date"]`).type(exampleSpaceEvent.date);
    cy.get(`[data-cy="description"]`).type(exampleSpaceEvent.description);
    cy.get('[data-cy="photo"]').eq(0).selectFile(exampleSpaceEvent.photo);
    cy.get("[data-cy='type']").select(exampleSpaceEvent.type);
    cy.get("[data-cy='mission']")
      .find('option') // Busca todas las opciones en el menú desplegable
      .last()
      .then(option => {
        const lastValue = option.val(); // Se obtiene el valor de la última opción
        cy.get("[data-cy='mission']").select(lastValue!); // Selecciona la opción obtenida
      });
    cy.get(entityCreateSaveButtonSelector).click();

    cy.wait('@postEntitySpaceEventRequest').then(({ response }) => {
      expect(response?.statusCode).to.equal(201);
    });

    cy.wait('@getEntitiesSpaceEventsRequest').then(({ response }) => {
      expect(response?.statusCode).to.equal(200);
    });
  });

  it('Ir a la pagina de entidades de Space Event y editar el Space Event creado anteriormente', () => {
    cy.clickOnEntityMenuItem('space-event');
    cy.get('[data-cy="entityEditButton"]').last().click();

    cy.get(`[data-cy="name"]`).clear();
    cy.get(`[data-cy="name"]`).type(exampleEditSpaceEvent.name);
    cy.get(`[data-cy="date"]`).clear();
    cy.get(`[data-cy="date"]`).type(exampleEditSpaceEvent.date);
    cy.get(`[data-cy="description"]`).clear();
    cy.get(`[data-cy="description"]`).type(exampleEditSpaceEvent.description);
    cy.get(entityCreateSaveButtonSelector).click();

    cy.wait('@putEntitySpaceEventRequest').then(({ response }) => {
      expect(response?.statusCode).to.equal(200);
    });

    cy.wait('@getEntitiesSpaceEventsRequest').then(({ response }) => {
      expect(response?.statusCode).to.equal(200);
    });
  });
});
