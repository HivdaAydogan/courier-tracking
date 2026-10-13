package com.hivda.couriertracking.config;

import com.hivda.couriertracking.dto.StoreData;
import com.hivda.couriertracking.entity.Store;
import com.hivda.couriertracking.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StoreDataInitializer implements CommandLineRunner {

    private final StoreRepository repository;
    private final ObjectMapper mapper;

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() > 0) {
            return;
        }

        ClassPathResource resource = new ClassPathResource("stores.json");

        List<StoreData> storeDataList = mapper.readValue(resource.getInputStream(),
                new TypeReference<List<StoreData>>() {});

        List<Store> stores = storeDataList.stream()
                .map(data -> new Store(
                        data.getName(),
                        data.getLat(),
                        data.getLng())).toList();

        repository.saveAll(stores);
    }

}
