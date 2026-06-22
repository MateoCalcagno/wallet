import { useState } from 'react'

export function useFirstVisit() {
  const [isFirstVisit] = useState(() => !localStorage.getItem('nova_visited'))

  const markVisited = () => localStorage.setItem('nova_visited', 'true')

  return { isFirstVisit, markVisited }
}