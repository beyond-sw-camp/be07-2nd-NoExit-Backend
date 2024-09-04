package com.E1i3.NoExit.domain.board.service;

import com.E1i3.NoExit.domain.board.domain.Board;
import com.E1i3.NoExit.domain.board.domain.BoardType;
import com.E1i3.NoExit.domain.board.dto.*;
import com.E1i3.NoExit.domain.board.repository.BoardRepository;
import com.E1i3.NoExit.domain.boardimage.domain.BoardImage;
import com.E1i3.NoExit.domain.boardimage.repository.BoardImageRepository;
import com.E1i3.NoExit.domain.common.domain.DelYN;
import com.E1i3.NoExit.domain.common.service.S3Service;
import com.E1i3.NoExit.domain.member.domain.Member;
import com.E1i3.NoExit.domain.member.repository.MemberRepository;
import com.E1i3.NoExit.domain.notification.controller.SseController;
import com.E1i3.NoExit.domain.notification.domain.NotificationType;
import com.E1i3.NoExit.domain.notification.dto.NotificationResDto;
import com.E1i3.NoExit.domain.notification.repository.NotificationRepository;
import com.E1i3.NoExit.domain.notification.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final S3Service s3Service;
    private final BoardImageRepository boardImageRepository;
    private static final String BOARD_PREFIX = "board:";
    private static final String MEMBER_PREFIX = "member:";
    private final SseController sseController;
    private final NotificationRepository notificationRepository;


    @Autowired

    public BoardService(BoardRepository boardRepository, MemberRepository memberRepository,
                        NotificationService notificationService, S3Service s3Service, BoardImageRepository boardImageRepository,
                        SseController sseController, NotificationRepository notificationRepository) {
        this.boardRepository = boardRepository;
        this.memberRepository = memberRepository;
        this.notificationService = notificationService;
        this.s3Service = s3Service;
        this.boardImageRepository = boardImageRepository;
        this.sseController = sseController;
        this.notificationRepository = notificationRepository;
    }

    @Autowired
    @Qualifier("4")
    private RedisTemplate<String, Object> boardRedisTemplate;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.folder.folderName5}")
    private String folder;

    public Board boardCreate(BoardCreateReqDto dto, List<MultipartFile> imgFiles) { // 게시글 생성
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("없는 회원입니다."));

        Board board = Board.builder()
                .member(member)
                .title(dto.getTitle())
                .contents(dto.getContents())
                .boardType(dto.getBoardType())
                .build();

        // 파일이 있는 경우 처리
        if (imgFiles != null && !imgFiles.isEmpty()) {
            for (MultipartFile f : imgFiles) {
                BoardImage img = BoardImage.builder()
                        .board(board)
                        .imageUrl(s3Service.uploadFile(f, "board"))
                        .build();
                board.getImgs().add(img);
                boardImageRepository.save(img);
            }
        }
        boardRepository.save(board);
        return board;
    }

    public Page<BoardListResDto> boardList(BoardSearchDto searchDto, Pageable pageable) { // 게시글 전체 조회

        if(searchDto == null) {
            Page<Board> boards = boardRepository.findByDelYN(pageable, DelYN.N);
            Page<BoardListResDto> boardListResDtos = boards.map(Board::fromEntity);
            return boardListResDtos;
        }

        Specification<Board> specification = new Specification<Board>() {
            @Override
            public Predicate toPredicate(Root<Board> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (searchDto.getSearchTitle() != null && !searchDto.getSearchTitle().isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("title"), "%" + searchDto.getSearchTitle() + "%"));
                }
                if (searchDto.getSearchContents() != null && !searchDto.getSearchContents().isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("contents"), "%" + searchDto.getSearchContents() + "%"));
                }
                if (searchDto.getSearchBoardType() != null && !searchDto.getSearchBoardType().isEmpty()) {
                    BoardType boardType = null;
                    try {
                        boardType = BoardType.valueOf(searchDto.getSearchBoardType());
                    } catch (IllegalArgumentException e) {
                        e.getMessage();
                    }
                    predicates.add(criteriaBuilder.equal(root.get("boardType"), boardType));
                }

                predicates.add(criteriaBuilder.equal(root.get("delYN"), DelYN.N));

                Predicate[] predicateArr = new Predicate[predicates.size()];
                for(int i=0; i<predicateArr.length; i++) {
                    predicateArr[i] = predicates.get(i);
                }

                Predicate predicate = criteriaBuilder.and(predicateArr);
                return predicate;
            }
        };

        Page<Board> boards = boardRepository.findAll(specification, pageable);
        Page<BoardListResDto> boardListResDtos = boards.map(Board::fromEntity);
        return boardListResDtos;
    }



    public BoardDetailResDto boardDetail(Long id) { // 특정 게시글 조회
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));
        if (board.getDelYN().equals(DelYN.Y)) {
            throw new IllegalArgumentException("cannot find board");
        }
        board.updateBoardHits(); // 조회수 +1
        return board.detailFromEntity();
    }

    public Board boardUpdate(Long id, BoardUpdateReqDto dto, List<MultipartFile> imgFiles) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("없는 회원입니다."));
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));

        if (!board.getMember().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 게시글만 수정할 수 있습니다.");
        }


        board.updateEntity(dto);
        if (imgFiles != null && !imgFiles.isEmpty()) {
            for (MultipartFile f : imgFiles) {
                BoardImage img = BoardImage.builder()
                        .board(board)
                        .imageUrl(s3Service.uploadFile(f, "board"))
                        .build();
                board.getImgs().add(img);
                boardImageRepository.save(img);
            }
        }

        return boardRepository.save(board);

    }

    public void boardDelete(Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));
        //        boardRepository.delete(board);
        if (!board.getMember().getEmail().equals(email)) {
            throw new IllegalArgumentException("본인의 게시글만 삭제할 수 있습니다.");
        } else if (board.getDelYN().equals(DelYN.Y)) {
            throw new IllegalArgumentException("cannot delete board");
        }
        board.deleteEntity();
        boardRepository.save(board);
    }

    @Transactional
    public boolean boardUpdateLikes(Long id) {
        boolean value = false;

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이메일입니다."));

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));

        String likesKey = BOARD_PREFIX + id + ":likes";
        String memberLikesKey = MEMBER_PREFIX + member.getId() + ":likes:" + id;

        Boolean isLiked = boardRedisTemplate.hasKey(memberLikesKey);
        System.out.println(isLiked);

        if (isLiked != null && isLiked) { // 이미 좋아요 누름
            boardRedisTemplate.delete(memberLikesKey);
            boardRedisTemplate.opsForSet().remove(likesKey, member.getId());
            board.updateLikes(false);
        } else {
            boardRedisTemplate.opsForValue().set(memberLikesKey, true);
            boardRedisTemplate.opsForSet().add(likesKey, member.getId());
            board.updateLikes(true);
            value = true;
            String receiver_email = board.getMember().getEmail();
            if(!receiver_email.equals(email)) {
                NotificationResDto notificationResDto = NotificationResDto.builder()
                        .notification_id(board.getId())
                        .email(receiver_email)
                        .sender_email(email)
                        .type(NotificationType.BOARD_LIKE)
                        .message(member.getNickname() + "님이 내 게시글을 추천합니다.").build();
                sseController.publishMessage(notificationResDto, receiver_email);
            }
        }


        boardRepository.save(board);
        return value;

    }



    @Transactional
    public boolean boardUpdateDislikes(Long id) {
        boolean value = false;

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이메일입니다."));

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + id));

        String dislikesKey = BOARD_PREFIX + id + ":dislikes";
        String memberDislikesKey = MEMBER_PREFIX + member.getId() + ":dislikes:" + id;

        Boolean isDisliked = boardRedisTemplate.hasKey(memberDislikesKey);

        if (isDisliked != null && isDisliked) {
            boardRedisTemplate.delete(memberDislikesKey);
            boardRedisTemplate.opsForSet().remove(dislikesKey, member.getId());
            board.updateDislikes(false);
        } else {
            boardRedisTemplate.opsForValue().set(memberDislikesKey, true);
            boardRedisTemplate.opsForSet().add(dislikesKey, member.getId());
            board.updateDislikes(true);
            value = true;
        }

        boardRepository.save(board);

        return value;
    }

}