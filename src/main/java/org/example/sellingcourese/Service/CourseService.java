
package org.example.sellingcourese.Service;

import com.nimbusds.jose.util.Resource;
import org.example.sellingcourese.Model.Course;
import org.example.sellingcourese.Request.ImgurResponse;
import org.example.sellingcourese.repository.CartDetailRepository;
import org.example.sellingcourese.repository.CourseRepository;
import org.example.sellingcourese.repository.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private OrderDetailRepository orderItemRepository;

    @Value("${imgur.client-id}") // Client ID của Imgur từ application.properties
    private String clientId;

    private final String IMGUR_API_URL = "https://api.imgur.com/3/image";

    // Upload file lên Imgur
    private String uploadToImgur(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null; // Nếu file null hoặc rỗng, trả về null
        }

        try {
            // Encode file thành Base64
            String encodedFile = Base64.getEncoder().encodeToString(file.getBytes());

            // Tạo request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "Client-ID " + clientId);

            // Tạo body của request
            String requestBody = "image=" + encodedFile;

            // Tạo HTTP request
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            // Gửi request đến Imgur
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<ImgurResponse> response = restTemplate.postForEntity(IMGUR_API_URL, requestEntity, ImgurResponse.class);

            // Kiểm tra kết quả
            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData().getLink(); // Trả về URL của hình ảnh trên Imgur
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to Imgur", e);
        }

        return null; // Nếu upload thất bại
    }

    // Add course with files
    public Course addCourseWithFiles(String title, String description, BigDecimal price, Long teacherId, Long categoryId,
                                     MultipartFile thumbnail, MultipartFile video) {
        String thumbnailUrl = uploadToImgur(thumbnail);
        String videoUrl = uploadToImgur(video); // Upload video giống như upload hình

        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setPrice(price);
        course.setTeacherId(teacherId);
        course.setCategoryId(categoryId);
        course.setThumbnailUrl(thumbnailUrl);
        course.setVideoUrl(videoUrl);
        course.setStatus(1); // Mặc định là chờ xử lý

        return courseRepository.save(course);
    }

    // Update course with files
    public Course updateCourseWithFiles(Long id, String title, String description, BigDecimal price, Long teacherId,
                                        Long categoryId, MultipartFile thumbnail, MultipartFile video) {
        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            course.setTitle(title);
            course.setDescription(description);
            course.setPrice(price);
            course.setTeacherId(teacherId);
            course.setCategoryId(categoryId);

            if (thumbnail != null && !thumbnail.isEmpty()) {
                course.setThumbnailUrl(uploadToImgur(thumbnail));
            }

            if (video != null && !video.isEmpty()) {
                course.setVideoUrl(uploadToImgur(video));
            }

            return courseRepository.save(course);
        } else {
            throw new RuntimeException("Course not found with ID: " + id);
        }
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found with ID: " + id);
        }

        // Xóa các CartDetails liên quan đến Course
        cartDetailRepository.deleteByCourseID(id);

        // Xóa các OrderItems liên quan đến Course
        orderItemRepository.deleteByCourseId(id);

        // Cuối cùng, xóa Course
        courseRepository.deleteById(id);
    }

    // Get course by ID
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + id));
    }

    // Find courses by title
    public List<Course> findCoursesByTitle(String title) {
        return courseRepository.findByTitleContainingIgnoreCase(title);
    }

//    public Resource getVideoStream(Long id) {
//        Course course = getCourseById(id);
//        String videoUrl = course.getVideoUrl();  // Lấy đường dẫn video từ course
//
//        // Kiểm tra nếu videoUrl không tồn tại
//        if (videoUrl == null || videoUrl.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not available for this course.");
//        }
//
//        try {
//            // Chuyển đường dẫn video thành một đối tượng Path
//            Path path = Paths.get(videoUrl).toAbsolutePath();  // Sử dụng videoUrl trực tiếp vì bạn đã lưu đường dẫn tuyệt đối
//            Resource resource = new UrlResource(path.toUri());  // Tạo UrlResource từ đường dẫn
//
//            // Kiểm tra nếu video tồn tại và có thể đọc được
//            if (resource.exists() && resource.isReadable()) {
//                return resource;
//            } else {
//                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Video file not found.");
//            }
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error loading video file.", e);
//        }
//    }

//    private String saveFile(MultipartFile file) {
//        if (file == null || file.isEmpty()) {
//            return null; // Nếu file null hoặc rỗng, trả về null
//        }
//        try {
//            // Kiểm tra đường dẫn thư mục
//            Path uploadDirPath = Paths.get(uploadDir).toAbsolutePath();
//            System.out.println("Upload directory: " + uploadDirPath);
//
//            // Lấy tên gốc của file
//            String originalFilename = file.getOriginalFilename();
//
//            // Tách phần mở rộng (nếu có)
//            String fileExtension = "";
//            if (originalFilename != null && originalFilename.contains(".")) {
//                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
//            }
//
//            // Tạo tên file mới bằng cách thêm timestamp
//            String newFilename = System.currentTimeMillis() + fileExtension;
//
//            // Đường dẫn file mới
//            Path filePath = Paths.get(uploadDir, newFilename).toAbsolutePath();
//
//            // Lưu file vào thư mục
//            Files.copy(file.getInputStream(), filePath);
//
//            // Trả về đường dẫn tuyệt đối của file
//            return filePath.toString();
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to store file: " + file.getOriginalFilename(), e);
//        }
//    }


    // Update course status
    public Course updateCourseStatus(Long id, Integer status) {
        Optional<Course> optionalCourse = courseRepository.findById(id);
        if (optionalCourse.isPresent()) {
            Course course = optionalCourse.get();
            course.setStatus(status);
            return courseRepository.save(course);
        } else {
            throw new RuntimeException("Course not found with ID: " + id);
        }
    }

    public Course updateCancelReason(Long id, String cancelReason,Integer status) {
        Optional<Course>optionalCourse=courseRepository.findById(id);
        if(optionalCourse.isPresent()){
            Course course=optionalCourse.get();
            course.setStatus(status);
            course.setCancelReason(cancelReason);
            return courseRepository.save(course);
        }else{
            throw new RuntimeException("Course not found with ID: " + id);
        }
    }

    // Get courses by status
    public List<Course> getCoursesByStatus(Integer status) {
        return courseRepository.findByStatus(status);
    }

    public List<Course>getCoursewithCategoryID(Long categoryID){
        List<Course>listcourse=courseRepository.findByCategoryId(categoryID);

        return listcourse;
    }

}