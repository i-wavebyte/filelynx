package backend.server.service.Service;

import backend.server.service.POJO.PageResponse;
import backend.server.service.Repository.CategorieRepository;
import backend.server.service.domain.Categorie;
import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Groupe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @Slf4j @Transactional
public class CategorieService implements ICategorieService{

    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private ICompagnieService compagnieService;
    public Categorie addCategorie(Categorie cat)
    {
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieName);
        cat.setCompagnie(compagnie);
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

    @Override
    public PageResponse<Categorie> getCategoriesPage(int page, int size, String sortBy, String sortOrder, String searchQuery) {
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Sort sort = Sort.by(direction, sortBy);
        int start = page * size;
        int end = Math.min(start + size, (int) categorieRepository.count());
        List<Categorie> categories = categorieRepository.findAllByCompagnieNom(compagnieName,sort);
        if (searchQuery != null && !searchQuery.isEmpty()){
            categories = categories.stream()
                    .filter(categorie -> categorie.getNom().toLowerCase().contains(searchQuery.toLowerCase()))
                    .collect(Collectors.toList());
        }
        List<Categorie> pageContent = categories.subList(start, Math.min(end, categories.size()));
        return new PageResponse<>(pageContent, categories.size());
    }


}
