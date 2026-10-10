package ru.mirea.pethotel.core.service;

//import ru.mirea.project.exception.BusinessRuleException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import ru.mirea.pethotel.core.entity.Enclosure;
import ru.mirea.pethotel.core.entity.EnclosureSize;
import ru.mirea.pethotel.core.exception.BusinessRuleException;
import ru.mirea.pethotel.core.exception.NotFoundException;
import ru.mirea.pethotel.core.repository.EnclosureRepository;
//import ru.mirea.project.repository.BookingRepository;
//import ru.mirea.project.repository.EnclosureRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EnclosureService {

//    private BookingRepository bookingRepo;
    private final EnclosureRepository enclosureRepo;

    public EnclosureService(/*BookingRepository bookingRepo*/EnclosureRepository enclosureRepo) {
//        this.bookingRepo = bookingRepo;
        this.enclosureRepo = enclosureRepo;
    }

    private void validateData(int enclosureNumber, EnclosureSize size) {
        if (enclosureNumber <= 0) {
            throw new BusinessRuleException("Номер вольера должен быть положительным числом");
        }
        if (size == null) {
            throw new BusinessRuleException("Размер вольера не может быть пустым");
        }
    }

    public Enclosure create(int number, EnclosureSize size) {
        validateData(number, size);
        if (enclosureRepo.findByNumber(number).isPresent()) {
            throw new BusinessRuleException("Вольер с номером " + number + " уже существует");
        }

        return enclosureRepo.save(new Enclosure(number, size));
    }

    public Enclosure update(Long enclosureId, int newNumber, EnclosureSize newSize) {
        validateData(newNumber, newSize);
        Enclosure enclosure = enclosureRepo.findById(enclosureId).orElseThrow(() -> new NotFoundException("Вольер с id " + enclosureId + " не найден"));
        Optional<Enclosure> sameNumber = enclosureRepo.findByNumber(newNumber);
        if (sameNumber.isPresent() && !sameNumber.get().getId().equals(enclosureId)) {
            throw new BusinessRuleException("Указанный номер " + newNumber + " уже используется");
        }

        enclosure.setNumber(newNumber);
        enclosure.setSize(newSize);


        return enclosureRepo.save(enclosure);
    }

    public void delete(Long enclosureId) {
        enclosureRepo
                .findById(enclosureId)
                .orElseThrow(() -> new NotFoundException("Вольер для удаления не найден"));
//          TODO
//        if (bookingRepo.existsByEnclosureId(enclosureId)) {
//            throw new BusinessRuleException("Невозможно удалить вольер с существующими бронированиями");
//        }
        enclosureRepo.deleteById(enclosureId);
    }

    public List<Enclosure> findAll() {
        return enclosureRepo.findAll();
    }

}
