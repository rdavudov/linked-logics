package io.linkedlogics.redis.repository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.redis.core.StringRedisTemplate;

import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.ProcessDefinitionReader;
import io.linkedlogics.model.ProcessDefinitionWriter;

public class ProcessRepository extends JedisRepository {
	private static final String PROCESS = "process:";
	private ConcurrentHashMap<String, String> processHashMap = new ConcurrentHashMap<>();

	public ProcessRepository(StringRedisTemplate redisTemplate) {
		super(redisTemplate);
	}
	
	public void set(ProcessDefinition process) throws Exception {
		redisTemplate.opsForValue().set(getKey(process), getValue(process));
	}

	public Optional<ProcessDefinition> get(String id, int version) throws Exception {
		String currentValue = redisTemplate.opsForValue().get(getKey(id, version));

		if (currentValue != null && !currentValue.isEmpty()) {
			String processHash = getMd5(currentValue);
			String key = getKey(id, version);

			if (!processHashMap.containsKey(key)) {
				processHashMap.put(key, processHash);
			} else {
				String hash = processHashMap.get(key);
				if (processHash.equals(hash)) {
					return Optional.empty();
				}
			}

			return Optional.of(getValue(currentValue));
		} 
		
		return Optional.empty();
	}

	public void delete(String id, int version) throws Exception {
		String currentValue = redisTemplate.opsForValue().get(getKey(id, version));
		if (currentValue != null && !currentValue.isEmpty()) {
			redisTemplate.delete(getKey(id, version));
		}
	}
	
	public void setVersion(String id, int version) {
		redisTemplate.opsForSet().add(getMaxVersionKey(id), String.valueOf(version));
	}
	
	public void deleteVersion(String id, int version) {
		redisTemplate.opsForSet().remove(getMaxVersionKey(id), String.valueOf(version));
	}
	
	public Optional<Integer> getMaxVersion(String id) {
		OptionalInt version = redisTemplate.opsForSet().members(getMaxVersionKey(id)).stream().mapToInt(s -> Integer.parseInt(s)).max();
		if (version.isPresent()) {
			return Optional.of(version.getAsInt());
		} else {
			return Optional.empty();
		}
	}

	private String getKey(ProcessDefinition process) {
		return PROCESS + process.getId() + ":" + process.getVersion();
	}

	private String getKey(String id, int version) {
		return PROCESS + id + ":" + version;
	}
	
	private String getMaxVersionKey(String id) {
		return PROCESS + id + ":MAX_VERSION";
	}

	private String getValue(ProcessDefinition process) throws Exception {
		return new ProcessDefinitionWriter(process).write();
	}

	private ProcessDefinition getValue(String process) throws Exception {
		return new ProcessDefinitionReader(process).read();
	}
	
	private String getMd5(String process) {
		try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(process.getBytes());
            byte[] md5Bytes = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte md5Byte : md5Bytes) {
                hexString.append(String.format("%02X", md5Byte));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
	}
}
