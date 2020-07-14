package app.ticket.dao;

import app.ticket.entity.Provider;
import app.ticket.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProviderDaoImpl implements ProviderDao{
    @Autowired
    ProviderRepository providerRepository;

    @Override
    public Provider getOne(Integer id) {
        return providerRepository.getOne(id);
    }
}
