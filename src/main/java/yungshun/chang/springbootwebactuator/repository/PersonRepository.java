package yungshun.chang.springbootwebactuator.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import yungshun.chang.springbootwebactuator.domain.Person;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {

}
