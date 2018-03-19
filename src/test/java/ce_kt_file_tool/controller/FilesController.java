package ce_kt_file_tool.controller;

import ce_kt_file_tool.entity.File;
import ce_kt_file_tool.repository.FilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@CrossOrigin
@Controller
@RequestMapping(path = "/files")
public class FilesController {
    private final FilesRepository filesRepository;

    @Autowired
    public FilesController(FilesRepository filesRepository)
    {
        this.filesRepository = filesRepository;
    }

    @GetMapping(path="")
    public @ResponseBody Iterable<File> findAll() {
        // This returns a JSON or XML with the users
        return filesRepository.findAll();
    }


    @GetMapping(path="/rbi")
    public @ResponseBody
    Iterable<File> findFirst(@RequestHeader("File") String name) {
        return StreamSupport.stream(filesRepository.findAll().spliterator(), false)
                .filter(m -> name.equals(m.getName())).collect(Collectors.toList());
    }
/*

    @GetMapping(path="/{id}")
    public @ResponseBody
    File getParameterDetails (@PathVariable(value="id") long id) {
        return filesRepository.findOne(id);
    }
    @PostMapping(path = "")
    public @ResponseBody String addNewDrawings(@RequestBody File drawing){


        filesRepository.save(drawing);
        return "Saved";
    }

    @ExceptionHandler
    void handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletResponse response) throws IOException {

        response.sendError(HttpStatus.BAD_REQUEST.value());

    }
    */
}
