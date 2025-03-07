package co.edu.uniandes.dse.parcial1.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uniandes.dse.parcial1.entities.EstacionEntity;
import co.edu.uniandes.dse.parcial1.entities.RutaEntity;
import co.edu.uniandes.dse.parcial1.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.parcial1.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.parcial1.repositories.EstacionRepository;
import co.edu.uniandes.dse.parcial1.repositories.RutaRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EstacionRutaService {
	@Autowired
	private RutaRepository rutaRepository;

	@Autowired
	private EstacionRepository estacionRepository;
		
		@Transactional
		public void removeEstacionRuta(Long idEstacion, Long idRuta) throws EntityNotFoundException, IllegalOperationException {
			Optional<RutaEntity> rutaEntity = rutaRepository.findById(idRuta);
			if (rutaEntity.isEmpty())
				throw new EntityNotFoundException("No se ha encontrado la ruta");

			Optional<EstacionEntity> estacionEntity = estacionRepository.findById(idEstacion);
			if (estacionEntity.isEmpty())
				throw new EntityNotFoundException("No se ha encontrado la estacion");
			
			List<RutaEntity> listaRutas = estacionEntity.get().getRutas();
			Integer contadorNocturno=0;
			for(RutaEntity ruta: listaRutas) {
				if(ruta.getTipo()=="Nocturna" || ruta.getTipo()=="nocturna" ) {
					contadorNocturno+=1;
				}
					
			}
			if (contadorNocturno<=2 && rutaEntity.get().getTipo()=="nocturna" ||rutaEntity.get().getTipo()=="nocturna") {
				throw new IllegalOperationException("No se puede eliminar esta ruta nocturna.");
			}
			
			rutaEntity.get().getEstaciones().remove(estacionEntity.get());
			estacionEntity.get().getRutas().remove(rutaEntity.get());
		
	}
	
	@Transactional
	public RutaEntity addEstacionRuta(Long idEstacion, Long idRuta) throws EntityNotFoundException, IllegalOperationException {
		Optional<RutaEntity> rutaEntity = rutaRepository.findById(idRuta);
		if (rutaEntity.isEmpty())
			throw new EntityNotFoundException("No se ha encontrado la ruta");

		Optional<EstacionEntity> estacionEntity = estacionRepository.findById(idEstacion);
		if (estacionEntity.isEmpty())
			throw new EntityNotFoundException("No se ha encontrado la estacion");
		
		List<RutaEntity> listaRutas = estacionEntity.get().getRutas();
		Integer contadorCirculares=0;
		for(RutaEntity ruta: listaRutas) {
			if(ruta.getTipo()=="Circular" || ruta.getTipo()=="circular" ) {
				contadorCirculares+=1;
			}
		}
		
		if (estacionEntity.get().getCapacidad() < 100 &&  contadorCirculares>=2) {
			throw new IllegalOperationException("No se puede agregar una ruta a la estacion porque tiene capacidad menos a 100 y no puede tener mas de 2 rutas circulares.");
		}
		
		rutaEntity.get().getEstaciones().add(estacionEntity.get());
		return rutaEntity.get();
	}
	

}
