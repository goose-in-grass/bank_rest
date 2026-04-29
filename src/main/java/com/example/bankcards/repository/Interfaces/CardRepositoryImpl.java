package com.example.bankcards.repository.Interfaces;

import com.example.bankcards.entity.CardImpl;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CardRepositoryImpl implements CardRepository {
    @Override
    public void flush() {

    }

    @Override
    public <S extends CardImpl> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends CardImpl> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<CardImpl> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public CardImpl getOne(Long aLong) {
        return null;
    }

    @Override
    public CardImpl getById(Long aLong) {
        return null;
    }

    @Override
    public CardImpl getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends CardImpl> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends CardImpl> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends CardImpl> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends CardImpl> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends CardImpl> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends CardImpl> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends CardImpl, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends CardImpl> S save(S entity) {
        return null;
    }

    @Override
    public <S extends CardImpl> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<CardImpl> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<CardImpl> findAll() {
        return List.of();
    }

    @Override
    public List<CardImpl> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(CardImpl entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends CardImpl> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<CardImpl> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<CardImpl> findAll(Pageable pageable) {
        return null;
    }
}
