package service;

import model.Product;
import repository.FavoriteRepository;

import java.util.List;

public class FavoriteService {
    private final FavoriteRepository favoriteRepo;

    public FavoriteService() {
        this.favoriteRepo = new FavoriteRepository();
    }

    public boolean addFavorite(String userID,
                               String productID) {
        if (favoriteRepo.exists(userID, productID)) {
            return false;
        }
        return favoriteRepo.save(userID, productID);
    }

    public boolean removeFavorite(String userID,
                                  String productID) {
        return favoriteRepo.delete(userID, productID);
    }

    public List<Product> getFavorites(String userID) {
        return favoriteRepo.findByUser(userID);
    }

    public boolean isFavorite(String userID,
                              String productID) {
        return favoriteRepo.exists(userID, productID);
    }
}
