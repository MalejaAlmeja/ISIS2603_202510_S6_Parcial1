package co.edu.uniandes.dse.parcial1.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import co.edu.uniandes.dse.parcial1.entities.EstacionEntity;
import co.edu.uniandes.dse.parcial1.entities.RutaEntity;
import co.edu.uniandes.dse.parcial1.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.parcial1.exceptions.IllegalOperationException;
import jakarta.transaction.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import({EstacionRutaService.class})
public class EstacionRutaServiceTest {
	@Autowired
	private EstacionRutaService estacionRutaService;
	
	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private EstacionEntity estacion = new EstacionEntity();
	private List<RutaEntity> rutaList = new ArrayList<>();

	
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from RutaEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from EstacionEntity").executeUpdate();
	}


	private void insertData() {

		estacion = factory.manufacturePojo(EstacionEntity.class);
		entityManager.persist(estacion);

		for (int i = 0; i < 3; i++) {
			RutaEntity entity = factory.manufacturePojo(RutaEntity.class);
			entity.getEstaciones().add(estacion);
			entityManager.persist(entity);
			rutaList.add(entity);
			estacion.getRutas().add(entity);
		}
	}
	@Test
	void removeEstacionRuta() throws EntityNotFoundException, IllegalOperationException{
		RutaEntity ruta = rutaList.getFirst();
		estacionRutaService.removeEstacionRuta(estacion.getId(), ruta.getId());
		assertFalse(estacion.getRutas().contains(ruta));
		
	}
	@Test
	void removeEstacionRutaEstacionInvalida() {
		assertThrows(EntityNotFoundException.class, () -> {
			RutaEntity nuevaRuta = factory.manufacturePojo(RutaEntity.class);
			entityManager.persist(nuevaRuta);
			estacionRutaService.addEstacionRuta(0L, nuevaRuta.getId());
		});
	}
	@Test
	void removeEstacionRutaRutaInvalida() {
		assertThrows(EntityNotFoundException.class, () -> {
			estacionRutaService.addEstacionRuta(estacion.getId(), 0L);
		});
		
	}
	@Test
	void removeEstacionRutaNocturna() {
		assertThrows(IllegalOperationException.class, () -> {
			RutaEntity nuevaRuta1 = factory.manufacturePojo(RutaEntity.class);
			nuevaRuta1.setTipo("Nocturna");
			entityManager.persist(nuevaRuta1);
			List<RutaEntity> listanueva = new ArrayList<>();
			listanueva.add(nuevaRuta1);
			estacion.setRutas(listanueva);
			estacionRutaService.removeEstacionRuta(estacion.getId(), nuevaRuta1.getId());
		});
		
	}
	
	
	@Test
	void addEstacionRuta() throws EntityNotFoundException, IllegalOperationException {
		RutaEntity nuevaRuta = factory.manufacturePojo(RutaEntity.class);
		
		RutaEntity rutaEntity = estacionRutaService.addEstacionRuta(estacion.getId(), nuevaRuta.getId());
		assertNotNull(rutaEntity);
		assertEquals(rutaEntity.getId(), nuevaRuta.getId());
		assertEquals(rutaEntity.getNombre(), nuevaRuta.getNombre());
		assertEquals(rutaEntity.getColor(), nuevaRuta.getColor());
		assertEquals(rutaEntity.getTipo(), nuevaRuta.getTipo());
	}
	
	@Test
	void addEstacionRutaEstacionInvalida() {
		assertThrows(EntityNotFoundException.class, () -> {
			RutaEntity nuevaRuta = factory.manufacturePojo(RutaEntity.class);
			entityManager.persist(nuevaRuta);
			estacionRutaService.addEstacionRuta(0L, nuevaRuta.getId());
		});
		
	}
	
	@Test
	void addEstacionRutaRutaInvalida() {
		assertThrows(EntityNotFoundException.class, () -> {
			estacionRutaService.addEstacionRuta(estacion.getId(), 0L);
		});
		
	}
	
	@Test
	void addEstacionRutaCircular() {
		assertThrows(IllegalOperationException.class, () -> {
			RutaEntity nuevaRuta1 = factory.manufacturePojo(RutaEntity.class);
			nuevaRuta1.setTipo("Circular");
			entityManager.persist(nuevaRuta1);
			RutaEntity nuevaRuta2 = factory.manufacturePojo(RutaEntity.class);
			nuevaRuta2.setTipo("Circular");
			entityManager.persist(nuevaRuta2);
			
			RutaEntity nuevaRuta3 = factory.manufacturePojo(RutaEntity.class);
			nuevaRuta3.setTipo("Circular");
			entityManager.persist(nuevaRuta3);
			
			rutaList.add(nuevaRuta1);
			rutaList.add(nuevaRuta2);
			estacion.setRutas(rutaList);
			estacion.setCapacidad(50);
			estacionRutaService.addEstacionRuta(estacion.getId(), nuevaRuta3.getId());
		});
	}
	
	
}
