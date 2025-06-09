package ${groupId}.service.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import ${groupId}.mapper.GenericMapper;
import ${groupId}.repository.GenericRepository;
import ${groupId}.exception.NoSuchElementFoundException;
import ${groupId}.service.GenericService;
import ${groupId}.util.response.CustomPage;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

public abstract class GenericServiceImpl<E, D, I> implements GenericService<E, D, I> {

    private static final Logger LOGGER = Logger.getLogger(GenericServiceImpl.class.getName());

    protected abstract GenericRepository<E, I> getRepo();
    protected abstract GenericMapper<E, D> getMapper();
    protected abstract I extractIdFromDto(D dto);

    @Override
    public D save(D dto) {
        E entity = getMapper().toEntity(dto);
        LOGGER.log(Level.INFO, "Saving entity: " + entity);
        entity = getRepo().save(entity);
        return getMapper().toDto(entity);
    }

    @Override
    public D update(D dto) {
        I id = extractIdFromDto(dto);
        Optional<E> optionalEntity = getRepo().findById(id);
        if (optionalEntity.isPresent()) {
            E entity = getMapper().toEntity(dto);
            LOGGER.log(Level.INFO, "Updating entity: " + entity);
            entity = getRepo().save(entity);
            return getMapper().toDto(entity);
        } else {
            throw new NoSuchElementFoundException("Entity with id " + id + " not found");
        }
    }

    @Override
    public List<D> findAll() {
        List<E> entities = getRepo().findAll();
        LOGGER.log(Level.INFO, "Retrieving all entities");
        return entities.stream()
                .map(getMapper()::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public D findById(I id) {
        if(id == null){
            throw new IllegalArgumentException("Id cannot be null");
        }
        Optional<E> optionalEntity = getRepo().findById(id);
        if (optionalEntity.isPresent()) {
            E entity = optionalEntity.get();
            LOGGER.log(Level.INFO, "Retrieving entity with id " + id + ": " + entity);
            return getMapper().toDto(entity);
        } else {
            throw new NoSuchElementFoundException("Entity with id " + id + " not found");
        }
    }

    @Override
    public void delete(I id) {
        if(id == null){
            throw new IllegalArgumentException("Id cannot be null");
        }
        Optional<E> optionalEntity = getRepo().findById(id);
        if (optionalEntity.isPresent()) {
            LOGGER.log(Level.INFO, "Deleting entity with id " + id);
            getRepo().deleteById(id);
        } else {
            throw new NoSuchElementFoundException("Entity with id " + id + " not found");
        }
    }

    @Override
    public CustomPage<D> findByAttributesAndPaginationAndSort(D dto, Pageable pageable, String sortField, Sort.Direction direction) {
        Sort sort = Sort.by(direction, sortField);
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        Page<D> page = getRepo().findAll(
                        getSpecificationFromFilter(dto), pageRequest)
                .map(getMapper()::toDto);
        return new CustomPage<>(page.getContent(), pageable, page.getTotalElements(),  page.getTotalPages(), sortField, direction);
    }

    @Override
    public long count(D dto) {
        Assert.notNull(dto, "DTO must not be null");
        Specification<E> specification = getSpecificationFromFilter(dto);
        return getRepo().count(specification);
    }

    protected Specification<E> getSpecificationFromFilter(D filter) {
        return (Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Class<?> filterClass = filter.getClass();
            Field[] fields = filterClass.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                Object value = null;
                try {
                    value = field.get(filter);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

                if (value != null) {
                    String fieldName = field.getName();
                    String[] fieldParts = fieldName.split("(?<=.)(?=\\p{Lu})");  // Split camel case
                    String entityFieldName = String.join(".", fieldParts).toLowerCase();

                    if (fieldName.endsWith("Id")) {
                        String entityReferencePath = entityFieldName.substring(0, entityFieldName.length() - 3); // Remove "id"
                        predicates.add(criteriaBuilder.equal(root.get(entityReferencePath).get("id"), value));
                    } else {
                        if (String.class.equals(field.getType())) {
                            String valueString = (String) value;
                            if(!valueString.isEmpty()){
                                valueString = '%' + valueString.toUpperCase() + '%';
                                Expression<String> stringExpression = root.get(entityFieldName).as(String.class);
                                Expression<String> stringExpressionUpper = criteriaBuilder.upper(stringExpression);
                                predicates.add(criteriaBuilder.like(stringExpressionUpper, valueString));
                            }
                        } else {
                            predicates.add(criteriaBuilder.equal(root.get(entityFieldName), value));
                        }
                    }
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
