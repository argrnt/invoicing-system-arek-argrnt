package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.WithId;
import pl.futurecollars.invoicing.service.FileService;
import pl.futurecollars.invoicing.service.IdService;
import pl.futurecollars.invoicing.service.JsonService;

@AllArgsConstructor
public class FileBasedDatabase<T extends WithId> implements Database<T> {

    private final Path databasePath;
    private final IdService idService;
    private final FileService fileService;
    private final JsonService jsonService;
    private final Class<T> clazz;

    @Override
    public long save(T item) {
        try {
            item.setId(idService.getNextId());
            fileService.appendLineToFile(databasePath, jsonService.objectToJson(item));
            return item.getId();
        } catch (IOException exception) {
            throw new RuntimeException("Database failed to save item", exception);
        }
    }

    @Override
    public Optional<T> getById(long id) {
        try {
            return fileService.readAllLines(databasePath)
                    .stream()
                    .filter(line -> containsId(line, id))
                    .map(line -> jsonService.stringToObject(line, clazz))
                    .findFirst();
        } catch (IOException exception) {
            throw new RuntimeException("Database failed to get item with id: " + id, exception);
        }
    }

    @Override
    public List<T> getAll() {
        try {
            return fileService.readAllLines(databasePath)
                    .stream()
                    .map(line -> jsonService.stringToObject(line, clazz))
                    .collect(Collectors.toList());
        } catch (IOException exception) {
            throw new RuntimeException("Failed to read items from file", exception);
        }
    }

    @Override
    public Optional<T> update(long id, T updatedItem) {
        try {
            List<String> allItems = fileService.readAllLines(databasePath);
            var itemsWithoutItemWithGivenId = allItems
                    .stream()
                    .filter(line -> !containsId(line, id))
                    .collect(Collectors.toList());

            updatedItem.setId(id);
            itemsWithoutItemWithGivenId.add(jsonService.objectToJson(updatedItem));

            fileService.writeLinesToFile(databasePath, itemsWithoutItemWithGivenId);
            allItems.removeAll(itemsWithoutItemWithGivenId);
            return allItems.isEmpty() ? Optional.empty()
                    : Optional.of(jsonService.stringToObject(allItems.get(0), clazz));

        } catch (IOException exception) {
            throw new RuntimeException("Failed to update item with id: " + id, exception);
        }
    }

    @Override
    public Optional<T> delete(long id) {
        try {
            var allItems = fileService.readAllLines(databasePath);
            var itemsExceptDeleted = allItems
                    .stream()
                    .filter(line -> !containsId(line, id))
                    .collect(Collectors.toList());

            fileService.writeLinesToFile(databasePath, itemsExceptDeleted);
            allItems.removeAll(itemsExceptDeleted);

            return allItems.isEmpty() ? Optional.empty() :
                    Optional.of(jsonService.stringToObject(allItems.get(0), clazz));

        } catch (IOException exception) {
            throw new RuntimeException("Failed to delete item with id: " + id, exception);
        }
    }

    private boolean containsId(String line, long id) {
        return line.contains("{\"id\":" + id + ",");
    }
}
