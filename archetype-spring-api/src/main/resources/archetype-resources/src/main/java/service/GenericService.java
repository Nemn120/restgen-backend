package ${groupId}.service;

import ${groupId}.util.response.CustomPage;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
public interface GenericService<E, D, I> {

	D save(D dto);
	D update(D dto);
	List<D> findAll();
	D findById(I id);
	void delete(I id);

	CustomPage<D> findByAttributesAndPaginationAndSort(D dto, Pageable pageable, String sortField , Sort.Direction direction);

	long count(D dto);
}

