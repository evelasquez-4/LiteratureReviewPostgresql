package literature.review.app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.model.VolumeNumbers;
import literature.review.app.repository.VolumeNumbersRepository;

@Service
public class VolumeNumbersService {

	@Autowired
	private VolumeNumbersRepository volume_number;
	
	public VolumeNumbers save(VolumeNumbers volumeNumber)
	{
		return this.volume_number.save(volumeNumber);
	}
	
	public Optional<VolumeNumbers> findById(Long id)
	{
		return this.volume_number.findById(id);
	}
	

	
	
	public Optional<VolumeNumbers> findByVolumeNumber(Integer volume, Integer number)
	{
		number = number == null?0:number;
		volume = volume == null?0:volume;
		
		if(number >0 && volume > 0)
			return volume_number.findByVolumeNumber(volume,number);
		else {
			
			if(number == 0)
				return this.volume_number.findByVolume(volume);
			else if(volume == 0)
				return this.volume_number.findByNumber(number);
			else
				return this.volume_number.findById((long)0);
		}
	}
}
