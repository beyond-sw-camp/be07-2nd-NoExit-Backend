package com.E1i3.NoExit.domain.boardimage.service;

import com.E1i3.NoExit.domain.board.domain.Board;
import com.E1i3.NoExit.domain.board.repository.BoardRepository;
import com.E1i3.NoExit.domain.boardimage.domain.BoardImage;
import com.E1i3.NoExit.domain.boardimage.repository.BoardImageRepository;
import com.E1i3.NoExit.domain.common.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class BoardImageService {

    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
    private final S3Service s3Service;

    @Autowired
    public BoardImageService(BoardRepository boardRepository, BoardImageRepository boardImageRepository, S3Service s3Service) {
        this.boardRepository = boardRepository;
        this.boardImageRepository = boardImageRepository;
        this.s3Service = s3Service;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.folder.folderName5}")
    private String folder;

    public void addImg(Long boardId, MultipartFile imgFile) {

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        BoardImage img = BoardImage.builder()
                .board(board)
                .imageUrl(s3Service.uploadFile(imgFile, "board"))
                .build();

        board.getImgs().add(img);
        boardRepository.save(board);
        boardImageRepository.save(img);
    }


    public void deleteImg(Long id) {

        BoardImage boardImage = boardImageRepository.findById(id).orElseThrow(()->new EntityNotFoundException("없는 이미지"));
        Board board = boardRepository.findById(boardImage.getBoard().getId())
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다."));

        boardImage.deleteEntity();
        boardRepository.save(board);
        boardImageRepository.save(boardImage);
    }
}