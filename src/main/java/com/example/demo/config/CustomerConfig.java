package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.example.demo.entity.CustomerInfo;
import com.example.demo.repository.CustomerRepository;

@Configuration
@EnableBatchProcessing
public class CustomerConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Autowired
	private CustomerRepository customerRepository;

	@Bean
	public FlatFileItemReader<CustomerInfo> readFile() {

		FlatFileItemReader<CustomerInfo> reader = new FlatFileItemReader<>();

		reader.setResource(new FileSystemResource("src\\main\\resources\\customer_info.csv"));
		reader.setName("csvReader");
		reader.setLinesToSkip(1);
		reader.setLineMapper(lineMapper());
		return reader;

	}

	private LineMapper<CustomerInfo> lineMapper() {
		DefaultLineMapper<CustomerInfo> lineMapper = new DefaultLineMapper<>();

		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setDelimiter(",");
		tokenizer.setStrict(false);
		tokenizer.setNames("id", "first_name", "last_name", "email", "gender", "contactNo", "country", "dob");

		BeanWrapperFieldSetMapper<CustomerInfo> fieldWrapper = new BeanWrapperFieldSetMapper<>();
		fieldWrapper.setTargetType(CustomerInfo.class);

		lineMapper.setLineTokenizer(tokenizer);
		lineMapper.setFieldSetMapper(fieldWrapper);

		return lineMapper;
	}

	@Bean
	public CustomerProcessor processor() {
		return new CustomerProcessor();
	}

	@Bean
	public RepositoryItemWriter<CustomerInfo> persistData() {

		RepositoryItemWriter<CustomerInfo> itemwriter = new RepositoryItemWriter<>();
		itemwriter.setRepository(customerRepository);
		itemwriter.setMethodName("save");
		return itemwriter;
	}

	@Bean
	public Step step1() {

		return stepBuilderFactory.get("csv-step").<CustomerInfo, CustomerInfo>chunk(10).reader(readFile())
				.processor(processor()).writer(persistData()).build();

	}

	@Bean
	public Job execute() {

		return jobBuilderFactory.get("csv-job").flow(step1()).end().build();
	}
}
