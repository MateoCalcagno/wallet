import { useEffect, useRef, useState, useCallback } from 'react'

function Spotlight({ targetRef, padding = 8 }) {
  const [rect, setRect] = useState(null)

  useEffect(() => {
    if (!targetRef?.current) return

    const update = () => {
      const r = targetRef.current?.getBoundingClientRect()
      if (r) setRect(r)
    }

    update()
    window.addEventListener('resize', update)
    return () => window.removeEventListener('resize', update)
  }, [targetRef])

  if (!rect) return null

  return (
    <div
      className="pointer-events-none absolute z-[51] transition-all duration-300 ease-in-out"
      style={{
        top: rect.top + window.scrollY - padding,
        left: rect.left + window.scrollX - padding,
        width: rect.width + padding * 2,
        height: rect.height + padding * 2,
        borderRadius: 10,
        boxShadow: '0 0 0 9999px rgba(0,0,0,0.70)',
        outline: '2px solid rgba(59,130,246,0.6)',
      }}
    />
  )
}

function Tooltip({ targetRef, placement = 'bottom', step, totalSteps, title, desc, onNext, onSkip, padding = 8 }) {
  const tooltipRef = useRef(null)
  const [pos, setPos] = useState({ top: 0, left: 0 })

  const reposition = useCallback(() => {
    if (!targetRef?.current || !tooltipRef?.current) return
    const r = targetRef.current.getBoundingClientRect()
    const t = tooltipRef.current.getBoundingClientRect()
    const margin = 14
    let top, left

    if (placement === 'right') {
      top = r.top + window.scrollY + r.height / 2 - t.height / 2
      left = r.left + window.scrollX + r.width + margin
    } else if (placement === 'bottom') {
      top = r.top + window.scrollY + r.height + margin + padding
      left = r.left + window.scrollX + r.width / 2 - t.width / 2
    } else if (placement === 'top') {
      top = r.top + window.scrollY - t.height - margin - padding
      left = r.left + window.scrollX + r.width / 2 - t.width / 2
    } else {
      top = r.top + window.scrollY + r.height / 2 - t.height / 2
      left = r.left + window.scrollX - t.width - margin
    }

    const vw = window.innerWidth
    left = Math.max(8, Math.min(left, vw - t.width - 8))
    top = Math.max(8, top)

    setPos({ top, left })
  }, [targetRef, placement, padding])

  useEffect(() => {
    reposition()
    window.addEventListener('resize', reposition)
    return () => window.removeEventListener('resize', reposition)
  }, [reposition])

  const isLast = step === totalSteps - 1

  return (
    <div
      ref={tooltipRef}
      className="fixed z-[52] w-80 bg-white rounded-2xl p-5 transition-all duration-300"
      style={{
        top: pos.top,
        left: pos.left,
        border: '0.5px solid rgba(0,0,0,0.08)',
        boxShadow: '0 4px 24px rgba(0,0,0,0.12)',
      }}
    >
      <p className="text-[11px] text-slate-400 mb-1.5">Paso {step + 1} de {totalSteps}</p>
      <p className="text-[15px] font-medium text-slate-800 mb-2">{title}</p>
      <p className="text-[13px] text-slate-500 leading-relaxed">{desc}</p>

      <div className="flex items-center justify-between mt-4">
        <div className="flex gap-1.5">
          {Array.from({ length: totalSteps }).map((_, i) => (
            <div
              key={i}
              className={`w-2 h-2 rounded-full transition-colors ${i === step ? 'bg-blue-500' : 'bg-slate-200'}`}
            />
          ))}
        </div>
        <div className="flex gap-2">
          {!isLast && (
            <button
              onClick={onSkip}
              className="px-3 py-1.5 text-[12px] text-slate-400 hover:text-slate-600 cursor-pointer"
            >
              Saltar
            </button>
          )}
          <button
            onClick={onNext}
            className="px-4 py-1.5 text-[12px] font-medium text-white bg-blue-500 hover:bg-blue-600 rounded-lg cursor-pointer transition"
          >
            {isLast ? '¡Entendido!' : 'Siguiente →'}
          </button>
        </div>
      </div>
    </div>
  )
}

export default function OnboardingTour({ steps, isOpen, currentStep, onNext, onEnd }) {
  if (!isOpen || !steps[currentStep]) return null

  const s = steps[currentStep]

  return (
    <>
      <div
        className="fixed inset-0 z-50 cursor-pointer"
        onClick={onEnd}
      />
      <Spotlight targetRef={s.ref} padding={s.padding ?? 8} />
      <Tooltip
        targetRef={s.ref}
        placement={s.placement ?? 'bottom'}
        step={currentStep}
        totalSteps={steps.length}
        title={s.title}
        desc={s.desc}
        padding={s.padding ?? 8}
        onNext={() => {
          if (currentStep < steps.length - 1) onNext()
          else onEnd()
        }}
        onSkip={onEnd}
      />
    </>
  )
}