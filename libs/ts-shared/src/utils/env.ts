export function getValidPort(
  portEnvValue: string | undefined,
  defaultPort: number,
): number {
  if (!portEnvValue) {
    return defaultPort;
  }
  const parsedPort = parseInt(portEnvValue, 10);
  if (!isNaN(parsedPort) && parsedPort >= 1 && parsedPort <= 65535) {
    return parsedPort;
  }
  return defaultPort;
}
