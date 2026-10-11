package ru.mirea.pethotel.core.service;

import org.springframework.stereotype.Service;
import ru.mirea.pethotel.core.entity.Owner;
import ru.mirea.pethotel.core.exception.NotFoundException;
import ru.mirea.pethotel.core.repository.BookingRepository;
import ru.mirea.pethotel.core.repository.OwnerRepository;
import ru.mirea.pethotel.core.exception.BusinessRuleException;
//import ru.mirea.project.repository.BookingRepository;

import java.util.List;

@Service
public class OwnerService {
    private final OwnerRepository ownerRepo;
    private final BookingRepository bookingRepo;

    public OwnerService(OwnerRepository ownerRepo, BookingRepository bookingRepo) {
        this.ownerRepo = ownerRepo;
        this.bookingRepo = bookingRepo;
    }

    public void validateLogin(String login) {
        if (login == null || login.isBlank()) {
            throw new BusinessRuleException("Логин не может быть пустым");
        }
        if (!login.matches("\\p{L}{1,12}")) {
            throw new BusinessRuleException(
                    "Логин должен содержать только буквы без пробелов и быть не длиннее 12 символов");
        }
    }

    public void validateFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new BusinessRuleException("Имя владельца не может быть пустым");
        }
        if (fullName.length() < 10) {
            throw new BusinessRuleException(
                    "Имя владельца должно содержать минимум 10 символов");
        }
        if (fullName.length() > 50) {
            throw new BusinessRuleException("Имя не может быть длиннее 50 символов");
        }
        if (!fullName.matches("\\p{L}+(?: \\p{L}+)*")) {
            throw new BusinessRuleException(
                    "Имя владельца должно содержать только буквы и пробелы между словами");
        }
    }

    public void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new BusinessRuleException("Телефон не может быть пустым");
        }
        if (!phone.matches("\\+?[0-9]{11,20}")) {
            throw new BusinessRuleException(
                    "Телефон должен содержать от 11 до 20 цифр, знак + допускается только в начале");
        }
    }

    private void validateOwnerData(String login, String fullName, String phone) {
        validateLogin(login);
        validateFullName(fullName);
        validatePhone(phone);
    }

    public Owner create(String login, String fullName, String phone) {
        validateOwnerData(login, fullName, phone);
        if (ownerRepo.existsByLogin(login)) {
            throw new BusinessRuleException("Владелец с таким логином уже существует");
        }
        if (ownerRepo.existsByPhone(phone)) {
            throw new BusinessRuleException("Владелец с таким телефоном уже существует");
        }
        Owner owner = new Owner(login, fullName, phone);
        return ownerRepo.save(owner);
    }

    public Owner getById(Long ownerId) {
        return ownerRepo.findById(ownerId).orElseThrow(() -> new NotFoundException("Владелец не найден"));
    }

    public Owner getByPhone(String phone) {
        return ownerRepo.findByPhone(phone).orElseThrow(() -> new NotFoundException("Владелец не найден"));
    }

    public List<Owner> getAll() {
        return ownerRepo.findAll();
    }

    public Owner update(Long ownerId, String newLogin, String newFullName, String newPhone) {
        validateOwnerData(newLogin, newFullName, newPhone);
        Owner owner = getById(ownerId);

        if (ownerRepo.existsByLoginAndIdNot(newLogin, ownerId)) {
            throw new BusinessRuleException("Владелец с таким логином уже существует");
        }
        if (ownerRepo.existsByPhoneAndIdNot(newPhone, ownerId)) {
            throw new BusinessRuleException("Владелец с таким телефоном уже существует");
        }

        owner.setFullName(newFullName);
        owner.setLogin(newLogin);
        owner.setPhone(newPhone);

        return ownerRepo.save(owner);
    }
    public void delete(Long ownerId) {
        getById(ownerId);
        if (!bookingRepo.findByPetOwnerId(ownerId).isEmpty()) {
            throw new BusinessRuleException("Невозможно удалить владельца с существующими бронированиями");
        }
        ownerRepo.deleteById(ownerId);
    }


}
