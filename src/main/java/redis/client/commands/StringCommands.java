package redis.client.commands;


public interface StringCommands {

  /**
   * <b><a href="http://redis.io/commands/set">Set Command</a></b>
   * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (1 GB).
   * <p>
   * Time complexity: O(1)
   * @param key
   * @param value
   * @return OK
   */
  String set(String key, String value);
  String get(String key);


}
