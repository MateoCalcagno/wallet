import { useState, useCallback } from 'react'

const TOUR_KEY = 'nova_tour_done'

export function useTour() {
  const alreadySeen = () => {
    try { return localStorage.getItem(TOUR_KEY) === '1' } catch { return false }
  }

  const [isOpen, setIsOpen] = useState(false)
  const [step, setStep] = useState(0)

  const startTour = useCallback(() => {
    setStep(0)
    setIsOpen(true)
  }, [])

  const endTour = useCallback(() => {
    setIsOpen(false)
    try { localStorage.setItem(TOUR_KEY, '1') } catch {}
  }, [])

  const nextStep = useCallback((totalSteps) => {
    setStep(prev => {
      if (prev < totalSteps - 1) return prev + 1
      return prev
    })
  }, [])

  return { isOpen, step, startTour, endTour, nextStep }
}