package io.github.pangju666.framework.data.mybatisplus.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.pangju666.commons.lang.utils.StreamUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseRepository<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {
	protected static final int DEFAULT_LIST_BATCH_SIZE = 500;

	public boolean existById(Serializable id) {
		return Objects.nonNull(getById(id));
	}

	public boolean notExistById(Serializable id) {
		return Objects.isNull(getById(id));
	}

	public <V> boolean existByColumn(SFunction<T, V> column, @Nullable V value) {
		Assert.notNull(column, "column 不可为空");
		if (Objects.isNull(value)) {
			return lambdaQuery()
				.isNull(column)
				.exists();
		}
		return lambdaQuery()
			.eq(column, value)
			.exists();
	}

	public <V> boolean notExistByColumn(SFunction<T, V> column, @Nullable V value) {
		Assert.notNull(column, "column 不可为空");
		if (Objects.isNull(value)) {
			return lambdaQuery()
				.isNotNull(column)
				.exists();
		}
		return !lambdaQuery()
			.eq(column, value)
			.exists();
	}

	public <V> List<?> listColumnValue(SFunction<T, V> column) {
		return listColumnValue(column, false, true);
	}

	public <V> List<?> listUniqueColumnValue(SFunction<T, V> column) {
		return listColumnValue(column, true, true);
	}

	public <V> List<?> listColumnValue(SFunction<T, V> column, boolean unique, boolean nonNull) {
		Assert.notNull(column, "column 不可为空");
		var queryWrapper = lambdaQuery().select(column);
		if (nonNull) {
			queryWrapper = queryWrapper.isNotNull(column);
		}
		var stream = queryWrapper.list()
			.stream()
			.map(column);
		if (unique) {
			stream = stream.distinct();
		}
		return stream.collect(Collectors.toList());
	}

	@Override
	public List<T> listByIds(Collection<? extends Serializable> ids) {
		return listByIds(ids, DEFAULT_LIST_BATCH_SIZE);
	}

	public List<T> listByIds(Collection<? extends Serializable> ids, int batchSize) {
		if (CollectionUtils.isEmpty(ids)) {
			return Collections.emptyList();
		}
		List<? extends Serializable> validIdList = StreamUtils.toNonNullList(ids);
		if (validIdList.size() <= batchSize) {
			return super.listByIds(validIdList);
		}
		return ListUtils.partition(new ArrayList<>(validIdList), batchSize)
			.stream()
			.map(super::listByIds)
			.flatMap(List::stream)
			.toList();
	}

	public <V> T getByColumnValue(SFunction<T, V> column, @Nullable V value) {
		Assert.notNull(column, "column 不可为空");
		if (Objects.isNull(value)) {
			return lambdaQuery()
				.isNull(column)
				.one();
		}
		return lambdaQuery()
			.eq(column, value)
			.one();
	}

	public <V> List<T> listByColumnValue(SFunction<T, V> column, @Nullable V value) {
		Assert.notNull(column, "column 不可为空");
		if (Objects.isNull(value)) {
			return lambdaQuery()
				.isNull(column)
				.list();
		}
		return lambdaQuery()
			.eq(column, value)
			.list();
	}

	public <V> List<T> listByColumnValues(SFunction<T, V> column, Collection<V> values) {
		return listByColumnValues(column, values, DEFAULT_LIST_BATCH_SIZE);
	}

	public <V> List<T> listByColumnValues(SFunction<T, V> column, Collection<V> values, int batchSize) {
		Assert.notNull(column, "column 不可为空");
		if (CollectionUtils.isEmpty(values)) {
			return Collections.emptyList();
		}
		List<?> validList = StreamUtils.toNonNullList(values);
		if (validList.size() <= batchSize) {
			return lambdaQuery()
				.in(column, validList)
				.list();
		}
		return ListUtils.partition(new ArrayList<>(validList), batchSize)
			.stream()
			.map(part -> lambdaQuery()
				.in(column, part)
				.list())
			.flatMap(List::stream)
			.toList();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveBatch(Collection<T> entityList) {
		if (CollectionUtils.isEmpty(entityList)) {
			return false;
		}
		List<T> validEntityList = StreamUtils.toNonNullList(entityList);
		return super.saveBatch(validEntityList, DEFAULT_BATCH_SIZE);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveBatch(Collection<T> entityList, int batchSize) {
		if (CollectionUtils.isEmpty(entityList)) {
			return false;
		}
		List<T> validEntityList = StreamUtils.toNonNullList(entityList);
		return super.saveBatch(validEntityList, batchSize);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean updateBatchById(Collection<T> entityList) {
		if (CollectionUtils.isEmpty(entityList)) {
			return false;
		}
		List<T> validEntityList = StreamUtils.toNonNullList(entityList);
		return super.updateBatchById(validEntityList, DEFAULT_BATCH_SIZE);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean updateBatchById(Collection<T> entityList, int batchSize) {
		if (CollectionUtils.isEmpty(entityList)) {
			return false;
		}
		List<T> validEntityList = StreamUtils.toNonNullList(entityList);
		return super.updateBatchById(validEntityList, batchSize);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveOrUpdateBatch(Collection<T> entityList) {
		if (CollectionUtils.isEmpty(entityList)) {
			return false;
		}
		List<T> validEntityList = StreamUtils.toNonNullList(entityList);
		return super.saveOrUpdateBatch(validEntityList, DEFAULT_BATCH_SIZE);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean saveOrUpdateBatch(Collection<T> entityList, int batchSize) {
		if (CollectionUtils.isEmpty(entityList)) {
			return false;
		}
		List<T> validEntityList = StreamUtils.toNonNullList(entityList);
		return super.saveOrUpdateBatch(validEntityList, batchSize);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean removeByIds(Collection<?> list) {
		if (CollectionUtils.isEmpty(list)) {
			return false;
		}
		List<?> validList = StreamUtils.toNonNullList(list);
		return super.removeByIds(validList);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean removeBatchByIds(Collection<?> list) {
		if (CollectionUtils.isEmpty(list)) {
			return false;
		}
		List<?> validList = StreamUtils.toNonNullList(list);
		return super.removeBatchByIds(validList, DEFAULT_BATCH_SIZE);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean removeBatchByIds(Collection<?> list, int batchSize) {
		if (CollectionUtils.isEmpty(list)) {
			return false;
		}
		List<?> validList = StreamUtils.toNonNullList(list);
		return super.removeBatchByIds(validList, batchSize);
	}

	public <V> boolean removeByColumnValue(SFunction<T, V> column, @Nullable V value) {
		Assert.notNull(column, "column 不可为空");
		if (Objects.isNull(value)) {
			return lambdaUpdate()
				.isNull(column)
				.remove();
		}
		return lambdaUpdate()
			.eq(column, value)
			.remove();
	}

	@Transactional(rollbackFor = Exception.class)
	public <V> boolean removeBatchByColumns(SFunction<T, V> column, Collection<V> values) {
		return removeBatchByColumns(column, values, DEFAULT_BATCH_SIZE);
	}

	@Transactional(rollbackFor = Exception.class)
	public <V> boolean removeBatchByColumns(SFunction<T, V> column, Collection<V> values, int batchSize) {
		Assert.notNull(column, "column 不可为空");
		if (CollectionUtils.isEmpty(values)) {
			return false;
		}
		List<V> validList = StreamUtils.toNonNullList(values);
		if (validList.size() <= batchSize) {
			return lambdaUpdate()
				.in(column, validList)
				.remove();
		}
		return ListUtils.partition(new ArrayList<>(validList), batchSize)
			.stream()
			.allMatch(part -> lambdaUpdate()
				.in(column, part)
				.remove());
	}
}