package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner(User owner);

    @Query("select it from Item it " +
            "where it.available = true and " +
            "(upper(it.name) like upper(concat('%', ?1, '%')) " +
            " or upper(it.description) like upper(concat('%', ?1, '%')))")
    List<Item> searchItem(String text);

    List<Item> findByRequestId(Long requestId);
}
