package ru.pt.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import ru.pt.domain.NumberGenerator;
import ru.pt.domain.Product;
import ru.pt.domain.ProductVersion;
import ru.pt.domain.productVersion.ProductVersionModel;
import ru.pt.domain.productVersion.PvPackage;
import ru.pt.exception.BadRequestException;
import ru.pt.repository.ProductRepository;
import ru.pt.repository.ProductVersionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVersionRepository productVersionRepository;
    private final NumberGeneratorService numberGeneratorService;
    public ProductService(ProductRepository productRepository,
                          ProductVersionRepository productVersionRepository,
                          NumberGeneratorService numberGeneratorService) {
        this.productRepository = productRepository;
        this.productVersionRepository = productVersionRepository;
        this.numberGeneratorService = numberGeneratorService;
    }

    public List<Map<String, Object>> listSummaries() {
        return productRepository.listActiveSummaries().stream()
                .map(r -> {
                    java.util.LinkedHashMap<String, Object> m = new java.util.LinkedHashMap<>();
                    m.put("id", r[0]);
                    m.put("lob", r[1]);
                    m.put("code", r[2]);
                    m.put("name", r[3]);
                    m.put("prodVersionNo", r[4]);
                    m.put("devVersionNo", r[5]);
                    return m;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductVersionModel create(ProductVersionModel productVersionModel) {
        if (productVersionModel.getLob() == null || productVersionModel.getLob().trim().isEmpty()) {
            throw new BadRequestException("lob must not be empty");
        }
        if (productVersionModel.getCode() == null || productVersionModel.getCode().trim().isEmpty()) {
            throw new BadRequestException("code must not be empty");
        }
        if (productVersionModel.getName() == null || productVersionModel.getName().trim().isEmpty()) {
            throw new BadRequestException("name must not be empty");
        }

        Integer id = productRepository.getNextProductId();

        Product product = new Product();
        product.setId(id);
        product.setProdVersionNo(null);
        product.setDevVersionNo(1);
        product.setDeleted(false);
        product.setLob(productVersionModel.getLob());
        product.setCode(productVersionModel.getCode());
        product.setName(productVersionModel.getName());
        productRepository.save(product);

        productVersionModel.setId(id);
        productVersionModel.setVersionNo(1);
        productVersionModel.setVersionStatus("DEV");
 
        if (productVersionModel.getQuoteValidator() == null) {
            productVersionModel.setQuoteValidator(new ArrayList<>());
        }
        if (productVersionModel.getSaveValidator() == null) {
            productVersionModel.setSaveValidator(new ArrayList<>());
        }
        if (productVersionModel.getPackages() == null ) {
            productVersionModel.setPackages(new ArrayList<>());
            }
        if (productVersionModel.getPackages().size() == 0) {
            PvPackage pvPackage = new PvPackage();
            pvPackage.setCode(0);
            pvPackage.setName("0");
            productVersionModel.getPackages().add(pvPackage);
            pvPackage.setCovers(new ArrayList<>());
        }

        ProductVersion pv = new ProductVersion();
        pv.setProductId(id);
        pv.setVersionNo(1);
        pv.setProduct(productVersionModel);
        
        productVersionRepository.save(pv);

        //if productVersion.getNumberGenerator() is not null, then create a new number generator
        if (productVersionModel.getNumberGenerator() != null) {
            NumberGenerator numberGenerator = new NumberGenerator();
            numberGenerator.setId(productVersionModel.getId());
            numberGenerator.setMask(productVersionModel.getNumberGenerator().getMask());
            numberGenerator.setMaxValue(productVersionModel.getNumberGenerator().getMaxValue());
            numberGenerator.setProductCode(productVersionModel.getCode());
            numberGenerator.setResetPolicy(productVersionModel.getNumberGenerator().getResetPolicy());

            numberGeneratorService.create(numberGenerator);

        }
        return productVersionModel;
    }

    public ProductVersionModel getVersion(Integer id, Integer versionNo) {
        ProductVersion pv = productVersionRepository.findByProductIdAndVersionNo(id, versionNo).orElse(null);
                
        ProductVersionModel productVersionModel = pv.getProduct();
        return productVersionModel;
    }

    @Transactional
    public ProductVersion createVersionFrom(Integer id, Integer versionNo) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getDevVersionNo() != null) {
            throw new IllegalArgumentException("only one version can be in dev status");
        }
        int newVersion = product.getProdVersionNo() == null ? 1 : product.getProdVersionNo() + 1;

        ProductVersionModel productVersionModel = productVersionRepository.findByProductIdAndVersionNo(id, versionNo)
                .orElseThrow(() -> new IllegalArgumentException("Base version not found"))
                .getProduct();

        productVersionModel.setVersionNo(newVersion);
        productVersionModel.setVersionStatus("DEV");

        ProductVersion pv = new ProductVersion();
        pv.setProductId(id);
        pv.setVersionNo(newVersion);
        pv.setProduct(productVersionModel);
        productVersionRepository.save(pv);

        product.setDevVersionNo(newVersion);
        productRepository.save(product);
        return pv;
    }

    @Transactional
    public ProductVersionModel updateVersion(Integer id, Integer versionNo, ProductVersionModel newProductVersionModel) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getDevVersionNo() == null || !product.getDevVersionNo().equals(versionNo)) {
            throw new IllegalArgumentException("only dev version can be updated");
        }

        newProductVersionModel.setId(id);
        newProductVersionModel.setVersionNo(versionNo);
        newProductVersionModel.setVersionStatus("DEV");

        ProductVersion pv = productVersionRepository.findByProductIdAndVersionNo(id, versionNo)
                .orElseThrow(() -> new IllegalArgumentException("Version not found"));
        pv.setProduct(newProductVersionModel);
        productVersionRepository.save(pv);

        if (newProductVersionModel.getNumberGenerator() != null) {
            NumberGenerator numberGenerator = new NumberGenerator();
            numberGenerator.setId(newProductVersionModel.getId());
            numberGenerator.setMask(newProductVersionModel.getNumberGenerator().getMask());
            numberGenerator.setMaxValue(newProductVersionModel.getNumberGenerator().getMaxValue());
            numberGenerator.setProductCode(newProductVersionModel.getCode());
            numberGenerator.setResetPolicy(newProductVersionModel.getNumberGenerator().getResetPolicy());

            numberGeneratorService.create(numberGenerator);

        }

        return newProductVersionModel;
    }

    @Transactional
    public void softDeleteProduct(Integer id) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.setDeleted(true);
        productRepository.save(product);
    }

    @Transactional
    public void deleteVersion(Integer id, Integer versionNo) {
        Product product = productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getDevVersionNo() == null || !product.getDevVersionNo().equals(versionNo)) {
            throw new IllegalArgumentException("only dev version can be deleted");
        }
        int deleted = productVersionRepository.deleteByProductIdAndVersionNo(id, versionNo);
        if (deleted == 0) {
            throw new IllegalArgumentException("Version not found");
        }
        product.setDevVersionNo(null);
        Integer pv = product.getProdVersionNo();
        product.setProdVersionNo(pv == null ? null : Math.max(0, pv));
        productRepository.save(product);
    }
    // get product by id
    public Product getProduct(Integer id) {
        return productRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }
    //get product by code and isDeletedFalse
    public Product getProductByCode(String code) {
        return productRepository.findByCodeAndIsDeletedFalse(code)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }


    
}


