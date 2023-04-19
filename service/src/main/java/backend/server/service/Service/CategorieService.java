package backend.server.service.Service;

import backend.server.service.Repository.CategorieRepository;
import backend.server.service.domain.Categorie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service @Slf4j @Transactional
public class CategorieService {

    @Autowired
    private CategorieRepository categorieRepository;

    public Categorie addCategorie(Categorie cat)
    {
        return categorieRepository.save(cat);
    }

    public void deleteCategorie(Categorie cat)
    {
        categorieRepository.delete(cat);
    }

    public Categorie updateCategorie(Categorie cat)
    {
        return categorieRepository.save(cat);
    }

    public List<Categorie> getAllCategories()
    {
        return categorieRepository.findAll();
    }

    public Categorie getCategorie(Long id)
    {
        return categorieRepository.findById(id).orElseThrow(()-> new RuntimeException("Category not found"));
    }

    public Categorie getCategorie(String nom)
    {

        try
        {
            return categorieRepository.findByNom(nom);
        }
        catch (Exception e)
        {
            return null;
        }
    }



}
