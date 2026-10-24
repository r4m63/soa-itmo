import { ApiError } from "@/lib/api-errors"

export async function request(baseUrl, method, path, body) {
  const response = await fetch(baseUrl + path, {
    method,
    headers: {
      Accept: "application/json",
      ...(body !== undefined && { "Content-Type": "application/json" }),
    },
    body: body !== undefined ? JSON.stringify(body) : undefined,
  })
  const data = await readJson(response)
  if (!response.ok) {
    throw new ApiError(response.status, data ?? { message: `Ошибка ${response.status}` })
  }
  return data
}

async function readJson(response) {
  if (response.status === 204 || !response.headers.get("Content-Type")?.includes("json")) return undefined
  try {
    return await response.json()
  } catch {
    return undefined
  }
}
