package com.ctzaf.dreamshops.service.image;

import com.ctzaf.dreamshops.dto.ImageDto;
import com.ctzaf.dreamshops.exceptions.ResourceNotFoundException;
import com.ctzaf.dreamshops.model.Image;
import com.ctzaf.dreamshops.model.Product;
import com.ctzaf.dreamshops.repository.ImageRepository;
import com.ctzaf.dreamshops.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {

    private final ImageRepository imageRepository;
    private final IProductService productService;

    /**
     * Finds an image by given id.
     *
     * @param id the id of the image to be found
     * @return the image with given id
     * @throws ResourceNotFoundException if image with given id is not found
     */
    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No Image not found with id: " + id));
    }

    /**
     * Deletes an image by given id.
     *
     * @param id the id of the image to be deleted
     * @throws ResourceNotFoundException if image with given id is not found
     */
    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id)
                .ifPresentOrElse(imageRepository::delete, () -> {
                    throw new ResourceNotFoundException("No Image not found with id: " + id);
                });
    }

    /**
     * Saves the given list of images to the database and returns a list of dtos containing the saved images' ids, filenames and download urls.
     *
     * @param files the list of images to be saved
     * @param productId the id of the product to which the images belong
     * @return a list of dtos containing the saved images' ids, filenames and download urls
     * @throws ResourceNotFoundException if no product with given id is found
     */
    @Override
    public List<ImageDto> saveImages(List<MultipartFile> files, Long productId) {
        Product product = productService.getProductById(productId);
        List<ImageDto> savedImageDto = new ArrayList<>();
        for(MultipartFile file : files) {
            try {
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

                String buildDownloadUrl = "/api/v1/images/image/download/";
                String downloadUrl = buildDownloadUrl + image.getId();
                image.setDownloadUrl(downloadUrl);
                Image savedImage = imageRepository.save(image);

                savedImage.setDownloadUrl(buildDownloadUrl + savedImage.getId());
                imageRepository.save(savedImage);

                ImageDto imageDto = new ImageDto();
                imageDto.setId(savedImage.getId());
                imageDto.setFileName(savedImage.getFileName());
                imageDto.setDownloadUrl(savedImage.getDownloadUrl());
                savedImageDto.add(imageDto);

            } catch (IOException | SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return savedImageDto;
    }

    /**
     * Updates the image with the given id by replacing its file with the given new file.
     *
     * @param file the new image file
     * @param imageId the id of the image to be updated
     * @throws ResourceNotFoundException if no image with given id is found
     */
    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        Image image = getImageById(imageId);
        try {
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}
