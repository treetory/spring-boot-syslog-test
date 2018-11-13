# spring-boot-syslog-test
Spring Boot Test Project. This Projects purpose is as follows.

1. try to control syslog server using syslog4j
2. try to store raw type logs to persistence layer such as DB, IMDG(in memory data grid)
3. try to parse raw type logs for converting normalized logs. (specific patterns that is defined already.)
4. try to test the performance of several scinarios from receiving logs to store logs

First of all, the samples is the logs of CounterACT.

Receiving syslog from CounterACT and parsing their logs.

It seems that CounterACT is not implement the sender using the format of RFC5234 definition.

<a href="https://tools.ietf.org/html/rfc5424" />

Second, to store logs in DB, using jOOQ to handle query and execute query.

Third, Apache ignite and REDIS is used to test caching DB or to store datas in there.
