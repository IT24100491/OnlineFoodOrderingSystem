package backend.controller;

import backend.exception.ItemNotFoundException;
import backend.model.ItemModel;
import backend.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/item-images/";

    @Autowired
    private ItemRepository itemRepository;

    // Add new item
    @PostMapping("")
    public ItemModel addItem(
            @RequestParam("itemDescription") String itemDescription,
            @RequestParam("itemRating") Double itemRating,
            @RequestParam("file") MultipartFile file
    ) {
        String fileName = file.getOriginalFilename();
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();
            file.transferTo(new File(UPLOAD_DIR + fileName));
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image: " + e.getMessage());
        }

        ItemModel item = new ItemModel(fileName, itemDescription, itemRating);
        return itemRepository.save(item);
    }

    // Get all items
    @GetMapping("")
    public List<ItemModel> getAllItems() {
        return itemRepository.findAll();
    }

    // Get single item by ID
    @GetMapping("/{id}")
    public ItemModel getItemById(@PathVariable Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
    }

    // Update item
    @PutMapping("/{id}")
    public ItemModel updateItem(
            @PathVariable Long id,
            @RequestParam("itemDescription") String itemDescription,
            @RequestParam("itemRating") Double itemRating,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        ItemModel existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        existingItem.setItemDescription(itemDescription);
        existingItem.setItemRating(itemRating);

        if (file != null && !file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            try {
                File dir = new File(UPLOAD_DIR);
                if (!dir.exists()) dir.mkdirs();
                file.transferTo(new File(UPLOAD_DIR + fileName));
                existingItem.setItemImage(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Error uploading image: " + e.getMessage());
            }
        }

        return itemRepository.save(existingItem);
    }

    // Delete item
    @DeleteMapping("/{id}")
    public String deleteItem(@PathVariable Long id) {
        ItemModel item = itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        String itemImage = item.getItemImage();
        if (itemImage != null) {
            File imageFile = new File(UPLOAD_DIR + itemImage);
            if (imageFile.exists()) imageFile.delete();
        }

        itemRepository.deleteById(id);
        return "Item deleted with id " + id;
    }

    // Get image by name
    @GetMapping("/image/{name}")
    public ResponseEntity<Resource> getImage(@PathVariable String name) throws MalformedURLException {
        Path path = Paths.get(UPLOAD_DIR + name);
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists()) return ResponseEntity.notFound().build();

        String contentType;
        try { contentType = Files.probeContentType(path); }
        catch (IOException e) { contentType = "application/octet-stream"; }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
    }
}
