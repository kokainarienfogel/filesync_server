package ce_kt_file_tool.repository;

import ce_kt_file_tool.entity.File;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilesRepository extends CrudRepository<File, Long> {

}
